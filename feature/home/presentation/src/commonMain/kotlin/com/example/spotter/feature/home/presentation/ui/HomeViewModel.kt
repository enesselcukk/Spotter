package com.example.spotter.feature.home.presentation.ui

import androidx.lifecycle.viewModelScope
import com.example.spotter.core.datastore.DefaultHomeViewMode
import com.example.spotter.core.datastore.ListSortOrder
import com.example.spotter.core.datastore.UserSettingsRepository
import com.example.spotter.core.domain.result.RestResult
import com.example.spotter.core.presentation.CoreViewModel
import com.example.spotter.feature.home.domain.model.SpotDto
import com.example.spotter.feature.home.domain.model.SpotSearchQuery
import com.example.spotter.feature.home.domain.repository.HomePreloadRepository
import com.example.spotter.feature.home.domain.usecase.GetNearbySpotsUseCase
import com.example.spotter.feature.home.domain.usecase.ResolveSearchLocationUseCase
import com.example.spotter.feature.home.domain.util.SpotListSorter
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlinx.coroutines.withTimeoutOrNull

class HomeViewModel(
    private val getNearbySpotsUseCase: GetNearbySpotsUseCase,
    private val resolveSearchLocationUseCase: ResolveSearchLocationUseCase,
    private val homePreloadRepository: HomePreloadRepository,
    private val userSettingsRepository: UserSettingsRepository,
) : CoreViewModel(), HomeActions {

    private var searchQuery: SpotSearchQuery = SpotSearchQuery.fallback()
    private var listSortOrder: ListSortOrder = ListSortOrder.DISTANCE
    private var defaultHomeViewMode: DefaultHomeViewMode = DefaultHomeViewMode.LIST
    private var rememberLastCategory: Boolean = true
    private var storedCategory: String = HomeCategories.CHARGING

    private val _uiState = MutableStateFlow<HomeUiState>(HomeUiState.Loading)
    val uiState: StateFlow<HomeUiState> = _uiState.asStateFlow()

    init {
        observeListPreferences()
        initializeFromPreload()
    }

    private fun observeListPreferences() {
        viewModelScope.launch {
            combine(
                userSettingsRepository.listSortOrder,
                userSettingsRepository.defaultHomeViewMode,
                userSettingsRepository.rememberLastCategory,
                userSettingsRepository.lastSelectedCategory,
            ) { sortOrder, viewMode, rememberCategory, lastCategory ->
                ListPreferenceSnapshot(
                    sortOrder = sortOrder,
                    defaultViewMode = viewMode,
                    rememberLastCategory = rememberCategory,
                    lastSelectedCategory = lastCategory,
                )
            }.collect { snapshot ->
                val previousDefaultViewMode = defaultHomeViewMode
                listSortOrder = snapshot.sortOrder
                defaultHomeViewMode = snapshot.defaultViewMode
                rememberLastCategory = snapshot.rememberLastCategory
                storedCategory = snapshot.lastSelectedCategory

                updateSuccess { current ->
                    val preferredCategory = if (rememberLastCategory && storedCategory in HomeUiState.defaultCategories) {
                        storedCategory
                    } else {
                        current.selectedCategory
                    }
                    val sortedSpots = SpotListSorter.sort(current.spots, listSortOrder)
                    val category = resolveCategoryWithResults(sortedSpots, preferredCategory)
                    val filtered = filterSpots(sortedSpots, category)
                    val viewMode = if (snapshot.defaultViewMode != previousDefaultViewMode) {
                        snapshot.defaultViewMode.toHomeViewMode()
                    } else {
                        current.viewMode
                    }
                    current.copy(
                        spots = sortedSpots,
                        viewMode = viewMode,
                        selectedCategory = category,
                        selectedSpotId = resolveSelectedSpotId(current, category, filtered),
                    )
                }
            }
        }
    }

    private fun initializeFromPreload() {
        val preload = homePreloadRepository.consume()
        if (preload != null) {
            searchQuery = preload.searchQuery
            _uiState.value = toSuccess(preload.spots)
            if (preload.spots.isEmpty() || preload.errorMessage != null) {
                loadSpots()
            }
            return
        }
        refreshLocationAndLoad()
    }

    fun refreshLocationAndLoad() {
        viewModelScope.launch {
            searchQuery = withTimeoutOrNull(LOCATION_TIMEOUT_MS) {
                resolveSearchLocationUseCase()
            } ?: SpotSearchQuery.fallback()
            _uiState.value = toSuccess(emptyList(), isLoadingSpots = true)
            loadSpots()
        }
    }

    override fun onCategorySelected(category: String) {
        viewModelScope.launch {
            if (rememberLastCategory) {
                userSettingsRepository.setLastSelectedCategory(category)
            }
        }
        updateSuccess { current ->
            val filtered = filterSpots(current.spots, category)
            current.copy(
                selectedCategory = category,
                selectedSpotId = filtered.firstOrNull()?.id,
            )
        }
    }

    override fun onSpotSelected(spotId: Long) {
        updateSuccess { it.copy(selectedSpotId = spotId) }
    }

    override fun onViewModeToggle() {
        updateSuccess { current ->
            val nextMode = when (current.viewMode) {
                HomeViewMode.List -> HomeViewMode.Map
                HomeViewMode.Map -> HomeViewMode.List
            }
            viewModelScope.launch {
                userSettingsRepository.setDefaultHomeViewMode(nextMode.toDefaultHomeViewMode())
            }
            current.copy(viewMode = nextMode)
        }
    }

    override fun onFavoriteToggle(spotId: Long) {
        updateSuccess { current ->
            val favorites = current.favoriteIds.toMutableSet()
            if (spotId in favorites) favorites.remove(spotId) else favorites.add(spotId)
            current.copy(favoriteIds = favorites)
        }
    }

    override fun onBottomNavSelected(nav: HomeBottomNav) {
        updateSuccess { it.copy(selectedBottomNav = nav) }
    }

    override fun retry() {
        refreshLocationAndLoad()
    }

    private fun loadSpots() {
        viewModelScope.launch {
            updateSuccess { it.copy(isLoadingSpots = true) }
            safeFlowApiCall { getNearbySpotsUseCase(searchQuery) }
                .collect { result ->
                    _uiState.value = when (result) {
                        is RestResult.Loading -> {
                            result.result?.let { toSuccess(it, isLoadingSpots = true) }
                                ?: (_uiState.value as? HomeUiState.Success)?.copy(isLoadingSpots = true)
                                ?: HomeUiState.Loading
                        }

                        is RestResult.Success -> toSuccess(result.result, isLoadingSpots = false)

                        is RestResult.Error -> {
                            result.result?.let { toSuccess(it, isLoadingSpots = false) }
                                ?: (_uiState.value as? HomeUiState.Success)?.copy(isLoadingSpots = false)
                                ?: HomeUiState.Error(message = result.error.message)
                        }
                    }
                }
        }
    }

    private fun toSuccess(
        spots: List<SpotDto>,
        isLoadingSpots: Boolean = false,
    ): HomeUiState.Success {
        val current = _uiState.value as? HomeUiState.Success
        val preferredCategory = resolveInitialCategory(current?.selectedCategory)
        val sortedSpots = SpotListSorter.sort(spots, listSortOrder)
        val selectedCategory = resolveCategoryWithResults(sortedSpots, preferredCategory)
        val filtered = filterSpots(sortedSpots, selectedCategory)

        return HomeUiState.Success(
            spots = sortedSpots,
            categories = HomeUiState.defaultCategories,
            selectedCategory = selectedCategory,
            viewMode = current?.viewMode ?: defaultHomeViewMode.toHomeViewMode(),
            selectedSpotId = resolveSelectedSpotId(current, selectedCategory, filtered),
            favoriteIds = current?.favoriteIds.orEmpty(),
            locationLabel = searchQuery.locationLabel,
            usesDeviceLocation = searchQuery.isDeviceLocation,
            userLatitude = searchQuery.latitude,
            userLongitude = searchQuery.longitude,
            selectedBottomNav = current?.selectedBottomNav ?: HomeBottomNav.Search,
            isLoadingSpots = isLoadingSpots,
        )
    }

    private fun resolveCategoryWithResults(spots: List<SpotDto>, preferred: String): String {
        if (filterSpots(spots, preferred).isNotEmpty()) return preferred
        return HomeCategories.ids.firstOrNull { category ->
            filterSpots(spots, category).isNotEmpty()
        } ?: preferred
    }

    private fun resolveInitialCategory(currentCategory: String?): String {
        if (currentCategory != null) return currentCategory
        if (rememberLastCategory && storedCategory in HomeUiState.defaultCategories) {
            return storedCategory
        }
        return HomeCategories.CHARGING
    }

    private fun resolveSelectedSpotId(
        current: HomeUiState.Success?,
        category: String,
        filtered: List<SpotDto> = filterSpots(current?.spots.orEmpty(), category),
    ): Long? = current?.selectedSpotId?.takeIf { id ->
        filtered.any { it.id == id }
    } ?: filtered.firstOrNull()?.id

    private fun filterSpots(spots: List<SpotDto>, category: String): List<SpotDto> {
        if (category == HomeCategories.ALL) return spots
        return spots.filter { it.amenity == category }
    }

    private inline fun updateSuccess(transform: (HomeUiState.Success) -> HomeUiState.Success) {
        _uiState.update { state ->
            if (state is HomeUiState.Success) transform(state) else state
        }
    }

    private data class ListPreferenceSnapshot(
        val sortOrder: ListSortOrder,
        val defaultViewMode: DefaultHomeViewMode,
        val rememberLastCategory: Boolean,
        val lastSelectedCategory: String,
    )

    private companion object {
        const val LOCATION_TIMEOUT_MS = 12_000L
    }
}
