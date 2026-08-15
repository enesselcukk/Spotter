package com.example.spotter.feature.map.presentation.ui

import androidx.lifecycle.viewModelScope
import com.example.spotter.core.datastore.UserSettingsRepository
import com.example.spotter.core.domain.result.RestResult
import com.example.spotter.core.navigation.NavigationCommand
import com.example.spotter.core.navigation.NavigationManager
import com.example.spotter.core.presentation.CoreViewModel
import com.example.spotter.feature.map.contract.MapScreenDestination
import com.example.spotter.feature.map.domain.model.RoutePlan
import com.example.spotter.feature.map.domain.model.RoutePoint
import com.example.spotter.feature.map.domain.model.TravelMode
import com.example.spotter.feature.map.domain.repository.MapSpotsHandoff
import com.example.spotter.feature.map.domain.repository.NavigationLocationTracker
import com.example.spotter.feature.map.domain.usecase.GetRoutePlanUseCase
import com.example.spotter.feature.map.domain.util.NavigationProgressCalculator
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class MapViewModel(
    private val getRoutePlanUseCase: GetRoutePlanUseCase,
    private val mapSpotsHandoff: MapSpotsHandoff,
    private val navigationLocationTracker: NavigationLocationTracker,
    private val userSettingsRepository: UserSettingsRepository,
    private val navigationManager: NavigationManager,
) : CoreViewModel(), MapActions {

    private val _uiState = MutableStateFlow(MapUiState())
    val uiState: StateFlow<MapUiState> = _uiState.asStateFlow()

    private var routeJob: Job? = null
    private var locationJob: Job? = null
    private var currentDestination: MapScreenDestination? = null

    init {
        viewModelScope.launch {
            userSettingsRepository.distanceUnit.collect { unit ->
                _uiState.update { it.copy(distanceUnit = unit) }
            }
        }
    }

    /**
     * View models are scoped above the back stack entry, so the screen re-arms itself whenever
     * it is opened with different arguments instead of only on first composition.
     */
    fun start(destination: MapScreenDestination) {
        if (currentDestination == destination) return
        currentDestination = destination
        stopNavigation()

        val allSpots = mapSpotsHandoff.peek()
        val spots = destination.category
            ?.let { category -> allSpots.filter { it.amenity == category } }
            ?.takeIf { it.isNotEmpty() }
            ?: allSpots

        val selectedSpotId = destination.focusedSpotId?.takeIf { id -> spots.any { it.id == id } }
            ?: spots.firstOrNull()?.id

        _uiState.update {
            it.copy(
                userLocation = RoutePoint(destination.userLatitude, destination.userLongitude),
                spots = spots,
                selectedSpotId = selectedSpotId,
            )
        }

        loadRoute()
    }

    override fun onSpotSelected(spotId: Long) {
        if (_uiState.value.isNavigating) return
        if (_uiState.value.selectedSpotId == spotId) return
        _uiState.update { it.copy(selectedSpotId = spotId, areStepsExpanded = false) }
        loadRoute()
    }

    override fun onTravelModeSelected(travelMode: TravelMode) {
        if (_uiState.value.isNavigating) return
        if (_uiState.value.travelMode == travelMode) return
        _uiState.update { it.copy(travelMode = travelMode) }
        loadRoute()
    }

    override fun onStepsToggle() {
        _uiState.update { it.copy(areStepsExpanded = !it.areStepsExpanded) }
    }

    override fun onRetryRoute() {
        loadRoute()
    }

    override fun onStartNavigation() {
        val state = _uiState.value
        val plan = state.routePlan ?: return
        if (state.isNavigating) return

        _uiState.update {
            it.copy(
                isNavigating = true,
                areStepsExpanded = false,
                navigationProgress = NavigationProgressCalculator.calculate(plan, it.userLocation),
            )
        }
        observeLocation()
    }

    override fun onStopNavigation() {
        stopNavigation()
    }

    override fun onBack() {
        if (_uiState.value.isNavigating) {
            stopNavigation()
            return
        }
        navigationManager.navigate(NavigationCommand.NavigateUp)
    }

    private fun stopNavigation() {
        locationJob?.cancel()
        locationJob = null
        _uiState.update { it.copy(isNavigating = false, navigationProgress = null) }
    }

    private fun observeLocation() {
        locationJob?.cancel()
        locationJob = viewModelScope.launch {
            navigationLocationTracker.locations().collect { point ->
                _uiState.update { current ->
                    if (!current.isNavigating) return@update current
                    val plan = current.routePlan ?: return@update current.copy(userLocation = point)
                    // Simulator / stale GPS can jump thousands of km away. Following that
                    // hides the route (iOS default is San Francisco) and falsely reports arrival.
                    if (!NavigationProgressCalculator.isOnRoute(plan.geometry, point)) {
                        return@update current
                    }
                    current.copy(
                        userLocation = point,
                        navigationProgress = NavigationProgressCalculator.calculate(plan, point),
                    )
                }
            }
        }
    }

    private fun loadRoute() {
        val state = _uiState.value
        val destination = state.selectedSpot ?: run {
            _uiState.update { it.copy(routePlan = null, isRouteLoading = false, hasRouteError = false) }
            return
        }

        routeJob?.cancel()
        routeJob = viewModelScope.launch {
            safeFlowApiCall {
                getRoutePlanUseCase(
                    origin = state.userLocation,
                    destination = RoutePoint(destination.lat, destination.lon),
                    travelMode = state.travelMode,
                )
            }.collect { result ->
                _uiState.update { current ->
                    when (result) {
                        is RestResult.Loading -> current.copy(
                            isRouteLoading = true,
                            hasRouteError = false,
                        )

                        is RestResult.Success -> current.copy(
                            routePlan = result.result.takeIf { it.hasUsableGeometry() },
                            isRouteLoading = false,
                            hasRouteError = !result.result.hasUsableGeometry(),
                        )

                        is RestResult.Error -> current.copy(
                            isRouteLoading = false,
                            hasRouteError = true,
                        )
                    }
                }
            }
        }
    }

    private fun RoutePlan.hasUsableGeometry(): Boolean = geometry.size > 1 && distanceMeters > 0
}
