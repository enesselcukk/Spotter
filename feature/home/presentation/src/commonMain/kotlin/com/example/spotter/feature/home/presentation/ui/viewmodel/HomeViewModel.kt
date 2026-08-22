package com.example.spotter.feature.home.presentation.ui.viewmodel

import androidx.lifecycle.viewModelScope
import com.example.spotter.core.datastore.UserSettingsRepository
import com.example.spotter.core.domain.result.RestResult
import com.example.spotter.core.navigation.NavigationManager
import com.example.spotter.core.presentation.CoreViewModel
import com.example.spotter.core.spotui.SpotSearchSuggestion
import com.example.spotter.core.spotui.SpotSearchSuggestionKind
import com.example.spotter.core.spotui.SpotterTab
import com.example.spotter.core.spotui.component.SpotCardBounds
import com.example.spotter.feature.favorites.domain.usecase.ObserveFavoriteIdsUseCase
import com.example.spotter.feature.favorites.domain.usecase.ToggleFavoriteUseCase
import com.example.spotter.feature.home.domain.model.SpotDto
import com.example.spotter.feature.home.domain.model.SpotSearchQuery
import com.example.spotter.feature.home.domain.repository.HomePreloadRepository
import com.example.spotter.feature.home.domain.usecase.GetNearbySpotsUseCase
import com.example.spotter.feature.home.domain.usecase.ResolveSearchLocationUseCase
import com.example.spotter.feature.home.presentation.navigate.HomeNavigator
import com.example.spotter.feature.home.presentation.state.HomeActions
import com.example.spotter.feature.home.presentation.state.HomeListPreferences
import com.example.spotter.feature.home.presentation.state.HomeUiReducer
import com.example.spotter.feature.home.presentation.state.HomeUiState
import com.example.spotter.feature.map.domain.model.RoutePoint
import com.example.spotter.feature.map.domain.repository.MapSpotsHandoff
import com.example.spotter.feature.map.domain.usecase.GetRoutePlanUseCase
import kotlinx.coroutines.channels.BufferOverflow
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlinx.coroutines.withTimeoutOrNull

class HomeViewModel(
    private val getNearbySpotsUseCase: GetNearbySpotsUseCase,
    private val resolveSearchLocationUseCase: ResolveSearchLocationUseCase,
    private val homePreloadRepository: HomePreloadRepository,
    private val userSettingsRepository: UserSettingsRepository,
    mapSpotsHandoff: MapSpotsHandoff,
    private val getRoutePlanUseCase: GetRoutePlanUseCase,
    navigationManager: NavigationManager,
    private val observeFavoriteIdsUseCase: ObserveFavoriteIdsUseCase,
    private val toggleFavoriteUseCase: ToggleFavoriteUseCase,
) : CoreViewModel(), HomeActions {

    private val navigator = HomeNavigator(navigationManager, mapSpotsHandoff)
    private val loadRequests = MutableSharedFlow<Unit>(
        extraBufferCapacity = 1,
        onBufferOverflow = BufferOverflow.DROP_OLDEST,
    )
    private val expandedRouteRequests = MutableSharedFlow<Long?>(
        extraBufferCapacity = 1,
        onBufferOverflow = BufferOverflow.DROP_OLDEST,
    )

    private var searchQuery = SpotSearchQuery.fallback()
    private var listPreferences = HomeListPreferences()

    private val _uiState = MutableStateFlow<HomeUiState>(HomeUiState.Loading)
    val uiState: StateFlow<HomeUiState> = _uiState.asStateFlow()

    private val currentSuccess: HomeUiState.Success?
        get() = _uiState.value as? HomeUiState.Success

    init {
        observeListPreferences()
        observeFavorites()
        observeSpotLoads()
        observeExpandedRoutes()
        initializeFromPreload()
    }

    override fun onCategorySelected(category: String) {
        persistLastCategory(category)
        updateSuccess { HomeUiReducer.selectCategory(it, category) }
    }

    override fun onSearchTextChanged(query: String) {
        updateSuccess { HomeUiReducer.searchTextChanged(it, query) }
    }

    override fun onSearchFocusChanged(active: Boolean) {
        updateSuccess { it.copy(isSearchActive = active) }
    }

    override fun onSearchClear() {
        updateSuccess { HomeUiReducer.searchTextChanged(it, "") }
    }

    override fun onSearchSuggestionSelected(suggestion: SpotSearchSuggestion) {
        if (suggestion.kind == SpotSearchSuggestionKind.Category) {
            suggestion.category?.let(::persistLastCategory)
        }
        updateSuccess { current ->
            HomeUiReducer.applySearchSuggestion(current, suggestion) ?: current
        }
    }

    override fun onSpotSelected(spotId: Long) {
        updateSuccess { it.copy(selectedSpotId = spotId) }
    }

    override fun onSpotCardClick(spotId: Long, sourceBounds: SpotCardBounds) {
        updateSuccess { HomeUiReducer.expandSpot(it, spotId, sourceBounds) }
        expandedRouteRequests.tryEmit(spotId)
    }

    override fun onExpandedSpotDismiss() {
        expandedRouteRequests.tryEmit(null)
        updateSuccess(HomeUiReducer::hideExpandedSpot)
    }

    override fun onExpandedSpotDismissAnimationEnd() {
        updateSuccess(HomeUiReducer::clearExpandedSpot)
    }

    override fun onNavigateToSpot(spotId: Long) {
        val current = currentSuccess ?: return
        expandedRouteRequests.tryEmit(null)
        updateSuccess(HomeUiReducer::dismissExpandedSpotForNavigation)
        navigator.openMap(current, focusedSpotId = spotId)
    }

    override fun onFavoriteToggle(spotId: Long) {
        val spot = currentSuccess?.spots?.find { it.id == spotId } ?: return
        viewModelScope.launch { toggleFavoriteUseCase(spot) }
    }

    override fun onTabSelected(tab: SpotterTab) {
        when (tab) {
            SpotterTab.Search -> return
            SpotterTab.Map -> currentSuccess?.let(navigator::openMap)
            SpotterTab.Favorites, SpotterTab.Settings -> navigator.switchRootTab(tab)
        }
    }

    override fun retry() {
        loadRequests.tryEmit(Unit)
    }

    private fun observeFavorites() {
        viewModelScope.launch {
            observeFavoriteIdsUseCase().collect { ids ->
                updateSuccess { it.copy(favoriteIds = ids) }
            }
        }
    }

    private fun observeListPreferences() {
        viewModelScope.launch {
            combine(
                userSettingsRepository.listSortOrder,
                userSettingsRepository.defaultHomeViewMode,
                userSettingsRepository.rememberLastCategory,
                userSettingsRepository.lastSelectedCategory,
                ::HomeListPreferences,
            ).collect { preferences ->
                listPreferences = preferences
                val current = currentSuccess ?: return@collect
                _uiState.value = HomeUiReducer.success(
                    spots = current.spots,
                    searchQuery = searchQuery,
                    preferences = preferences,
                    current = current,
                    isLoadingSpots = current.isLoadingSpots,
                    preferredCategory = preferences.preferredCategory,
                )
            }
        }
    }

    private fun observeSpotLoads() {
        viewModelScope.launch {
            loadRequests.collectLatest {
                searchQuery = withTimeoutOrNull(LOCATION_TIMEOUT_MS) {
                    resolveSearchLocationUseCase()
                } ?: SpotSearchQuery.fallback()
                _uiState.value = presentSpots(emptyList(), isLoadingSpots = true)
                collectSpots()
            }
        }
    }

    private fun observeExpandedRoutes() {
        viewModelScope.launch {
            expandedRouteRequests.collectLatest { spotId ->
                if (spotId == null) return@collectLatest
                collectExpandedRoute(spotId)
            }
        }
    }

    private fun initializeFromPreload() {
        val preload = homePreloadRepository.consume()
        if (preload == null || preload.spots.isEmpty()) {
            loadRequests.tryEmit(Unit)
            return
        }

        searchQuery = preload.searchQuery
        _uiState.value = presentSpots(preload.spots)
    }

    private suspend fun collectSpots() {
        updateSuccess { it.copy(isLoadingSpots = true) }
        safeFlowApiCall { getNearbySpotsUseCase(searchQuery) }
            .collect { result ->
                _uiState.value = HomeUiReducer.fromSpotResult(
                    result = result,
                    searchQuery = searchQuery,
                    preferences = listPreferences,
                    current = currentSuccess,
                )
            }
    }

    private suspend fun collectExpandedRoute(spotId: Long) {
        val current = currentSuccess ?: return
        val spot = current.spots.find { it.id == spotId } ?: return
        val origin = RoutePoint(current.userLatitude, current.userLongitude)
        val destination = RoutePoint(spot.lat, spot.lon)

        getRoutePlanUseCase(origin = origin, destination = destination).collect { result ->
            if (currentSuccess?.expandedSpotId != spotId) return@collect
            when (result) {
                is RestResult.Loading -> Unit
                is RestResult.Success -> updateSuccess {
                    HomeUiReducer.routeLoaded(it, result.result.geometry)
                }
                is RestResult.Error -> updateSuccess {
                    HomeUiReducer.routeLoaded(it, listOf(origin, destination))
                }
            }
        }
    }

    private fun persistLastCategory(category: String) {
        if (!listPreferences.rememberLastCategory) return
        viewModelScope.launch { userSettingsRepository.setLastSelectedCategory(category) }
    }

    private fun presentSpots(
        spots: List<SpotDto>,
        isLoadingSpots: Boolean = false,
    ): HomeUiState.Success = HomeUiReducer.success(
        spots = spots,
        searchQuery = searchQuery,
        preferences = listPreferences,
        current = currentSuccess,
        isLoadingSpots = isLoadingSpots,
    )

    private inline fun updateSuccess(transform: (HomeUiState.Success) -> HomeUiState.Success) {
        _uiState.update { state ->
            if (state is HomeUiState.Success) transform(state) else state
        }
    }

    private companion object {
        const val LOCATION_TIMEOUT_MS = 12_000L
    }
}
