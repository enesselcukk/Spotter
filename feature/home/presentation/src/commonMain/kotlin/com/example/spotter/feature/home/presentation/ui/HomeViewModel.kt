package com.example.spotter.feature.home.presentation.ui

import androidx.lifecycle.viewModelScope
import com.example.spotter.core.datastore.DefaultHomeViewMode
import com.example.spotter.core.datastore.ListSortOrder
import com.example.spotter.core.datastore.UserSettingsRepository
import com.example.spotter.core.domain.result.RestResult
import com.example.spotter.core.navigation.NavigationCommand
import com.example.spotter.core.navigation.NavigationManager
import com.example.spotter.core.navigation.switchTab
import com.example.spotter.core.presentation.CoreViewModel
import com.example.spotter.core.spotui.SpotCategories
import com.example.spotter.core.spotui.SpotSearchSuggestion
import com.example.spotter.core.spotui.SpotSearchSuggestionKind
import com.example.spotter.core.spotui.SpotterTab
import com.example.spotter.core.spotui.filterByCategoryAndSearch
import com.example.spotter.feature.favorites.contract.FavoritesScreenDestination
import com.example.spotter.feature.favorites.domain.usecase.ObserveFavoriteIdsUseCase
import com.example.spotter.feature.favorites.domain.usecase.ToggleFavoriteUseCase
import com.example.spotter.feature.home.contract.HomeScreenDestination
import com.example.spotter.feature.home.domain.model.SpotDto
import com.example.spotter.feature.home.domain.model.SpotSearchQuery
import com.example.spotter.feature.home.domain.repository.HomePreloadRepository
import com.example.spotter.feature.home.domain.usecase.GetNearbySpotsUseCase
import com.example.spotter.feature.home.domain.usecase.ResolveSearchLocationUseCase
import com.example.spotter.feature.home.domain.util.SpotListSorter
import com.example.spotter.feature.map.contract.MapScreenDestination
import com.example.spotter.feature.map.domain.repository.MapSpotsHandoff
import com.example.spotter.feature.settings.contract.SettingsScreenDestination
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
    private val mapSpotsHandoff: MapSpotsHandoff,
    private val navigationManager: NavigationManager,
    private val observeFavoriteIdsUseCase: ObserveFavoriteIdsUseCase,
    private val toggleFavoriteUseCase: ToggleFavoriteUseCase,
) : CoreViewModel(), HomeActions {

    private var searchQuery = SpotSearchQuery.fallback()
    private var listSortOrder = ListSortOrder.DISTANCE
    private var defaultHomeViewMode = DefaultHomeViewMode.LIST
    private var rememberLastCategory = true
    private var storedCategory = SpotCategories.CHARGING
    private var autoOpenedMap = false
    private val recentSearchQueries = mutableListOf<String>()

    private val _uiState = MutableStateFlow<HomeUiState>(HomeUiState.Loading)
    val uiState: StateFlow<HomeUiState> = _uiState.asStateFlow()

    private val currentSuccess: HomeUiState.Success?
        get() = _uiState.value as? HomeUiState.Success

    init {
        observeListPreferences()
        observeFavorites()
        initializeFromPreload()
    }

    override fun onCategorySelected(category: String) {
        if (rememberLastCategory) {
            viewModelScope.launch { userSettingsRepository.setLastSelectedCategory(category) }
        }
        updateSuccess { current ->
            current.copy(
                selectedCategory = category,
                selectedSpotId = filterSpots(current.spots, category, current.searchText).firstOrNull()?.id,
            )
        }
    }

    override fun onSearchTextChanged(query: String) {
        updateSuccess { current ->
            current.copy(
                searchText = query,
                selectedSpotId = filterSpots(current.spots, current.selectedCategory, query).firstOrNull()?.id,
            )
        }
    }

    override fun onSearchFocusChanged(active: Boolean) {
        updateSuccess { it.copy(isSearchActive = active) }
    }

    override fun onSearchClear() {
        updateSuccess { current ->
            current.copy(
                searchText = "",
                selectedSpotId = filterSpots(current.spots, current.selectedCategory, "").firstOrNull()?.id,
            )
        }
    }

    override fun onSearchSuggestionSelected(suggestion: SpotSearchSuggestion) {
        when (suggestion.kind) {
            SpotSearchSuggestionKind.Spot -> {
                val spot = currentSuccess?.spots?.find { it.id == suggestion.spotId } ?: return
                rememberRecentSearch(suggestion.label)
                updateSuccess { current ->
                    current.copy(
                        searchText = suggestion.label,
                        selectedCategory = spot.amenity ?: current.selectedCategory,
                        selectedSpotId = spot.id,
                        isSearchActive = false,
                    )
                }
            }

            SpotSearchSuggestionKind.Recent -> {
                updateSuccess { current ->
                    current.copy(
                        searchText = suggestion.label,
                        isSearchActive = false,
                        selectedSpotId = filterSpots(
                            current.spots,
                            current.selectedCategory,
                            suggestion.label,
                        ).firstOrNull()?.id,
                    )
                }
            }

            SpotSearchSuggestionKind.Category -> {
                val category = suggestion.category ?: return
                if (rememberLastCategory) {
                    viewModelScope.launch { userSettingsRepository.setLastSelectedCategory(category) }
                }
                rememberRecentSearch(suggestion.label)
                updateSuccess { current ->
                    current.copy(
                        searchText = suggestion.label,
                        selectedCategory = category,
                        selectedSpotId = filterSpots(current.spots, category, suggestion.label).firstOrNull()?.id,
                        isSearchActive = false,
                    )
                }
            }
        }
    }

    override fun onSpotSelected(spotId: Long) {
        updateSuccess { it.copy(selectedSpotId = spotId) }
    }

    override fun onOpenMap() {
        val current = currentSuccess ?: return

        mapSpotsHandoff.publish(current.spots)
        navigationManager.navigate(
            NavigationCommand.NavigateTo(
                to = MapScreenDestination(
                    userLatitude = current.userLatitude,
                    userLongitude = current.userLongitude,
                    focusedSpotId = current.selectedSpotId,
                    category = current.selectedCategory,
                ),
            ),
        )
    }

    override fun onFavoriteToggle(spotId: Long) {
        val spot = currentSuccess?.spots?.find { it.id == spotId } ?: return
        viewModelScope.launch { toggleFavoriteUseCase(spot) }
    }

    override fun onTabSelected(tab: SpotterTab) {
        val target = when (tab) {
            SpotterTab.Search -> return
            SpotterTab.Map -> {
                onOpenMap()
                return
            }
            SpotterTab.Favorites -> FavoritesScreenDestination
            SpotterTab.Settings -> SettingsScreenDestination
        }
        navigationManager.switchTab(
            target = target,
            root = HomeScreenDestination,
            currentIsRoot = true,
        )
    }

    override fun retry() {
        refreshLocationAndLoad()
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
            ) { sortOrder, viewMode, rememberCategory, lastCategory ->
                ListPreferenceSnapshot(sortOrder, viewMode, rememberCategory, lastCategory)
            }.collect { snapshot ->
                listSortOrder = snapshot.sortOrder
                defaultHomeViewMode = snapshot.defaultViewMode
                rememberLastCategory = snapshot.rememberLastCategory
                storedCategory = snapshot.lastSelectedCategory

                val current = currentSuccess ?: return@collect
                _uiState.value = buildSuccess(
                    spots = current.spots,
                    isLoadingSpots = current.isLoadingSpots,
                    preferredCategory = preferredStoredCategory(),
                )
            }
        }
    }

    private fun initializeFromPreload() {
        val preload = homePreloadRepository.consume() ?: return refreshLocationAndLoad()

        searchQuery = preload.searchQuery
        _uiState.value = buildSuccess(preload.spots)
        if (preload.spots.isEmpty() || preload.errorMessage != null) {
            loadSpots()
        } else {
            maybeAutoOpenMap()
        }
    }

    private fun refreshLocationAndLoad() {
        viewModelScope.launch {
            searchQuery = withTimeoutOrNull(LOCATION_TIMEOUT_MS) {
                resolveSearchLocationUseCase()
            } ?: SpotSearchQuery.fallback()
            _uiState.value = buildSuccess(emptyList(), isLoadingSpots = true)
            loadSpots()
        }
    }

    private fun loadSpots() {
        viewModelScope.launch {
            updateSuccess { it.copy(isLoadingSpots = true) }
            safeFlowApiCall { getNearbySpotsUseCase(searchQuery) }
                .collect { result ->
                    _uiState.value = when (result) {
                        is RestResult.Loading -> result.result?.let { buildSuccess(it, isLoadingSpots = true) }
                            ?: currentSuccess?.copy(isLoadingSpots = true)
                            ?: HomeUiState.Loading

                        is RestResult.Success -> buildSuccess(result.result)

                        is RestResult.Error -> result.result?.let { buildSuccess(it) }
                            ?: currentSuccess?.copy(isLoadingSpots = false)
                            ?: HomeUiState.Error(message = result.error.message)
                    }
                    maybeAutoOpenMap()
                }
        }
    }

    private fun maybeAutoOpenMap() {
        if (autoOpenedMap || defaultHomeViewMode != DefaultHomeViewMode.MAP) return
        if (currentSuccess?.spots.isNullOrEmpty()) return

        autoOpenedMap = true
        onOpenMap()
    }

    private fun buildSuccess(
        spots: List<SpotDto>,
        isLoadingSpots: Boolean = false,
        preferredCategory: String? = null,
    ): HomeUiState.Success {
        val current = currentSuccess
        val sortedSpots = SpotListSorter.sort(spots, listSortOrder)
        val category = resolveCategoryWithResults(
            spots = sortedSpots,
            preferred = preferredCategory ?: current?.selectedCategory ?: preferredStoredCategory(),
            searchText = current?.searchText.orEmpty(),
        )
        val filtered = filterSpots(sortedSpots, category, current?.searchText.orEmpty())

        return HomeUiState.Success(
            spots = sortedSpots,
            categories = HomeUiState.defaultCategories,
            selectedCategory = category,
            selectedSpotId = current?.selectedSpotId?.takeIf { id -> filtered.any { it.id == id } }
                ?: filtered.firstOrNull()?.id,
            favoriteIds = current?.favoriteIds.orEmpty(),
            locationLabel = searchQuery.locationLabel,
            usesDeviceLocation = searchQuery.isDeviceLocation,
            userLatitude = searchQuery.latitude,
            userLongitude = searchQuery.longitude,
            isLoadingSpots = isLoadingSpots,
            searchText = current?.searchText.orEmpty(),
            isSearchActive = current?.isSearchActive ?: false,
            recentSearchQueries = recentSearchQueries.toList(),
        )
    }

    private fun resolveCategoryWithResults(
        spots: List<SpotDto>,
        preferred: String,
        searchText: String,
    ): String {
        if (filterSpots(spots, preferred, searchText).isNotEmpty()) return preferred
        return SpotCategories.ids.firstOrNull { filterSpots(spots, it, searchText).isNotEmpty() } ?: preferred
    }

    private fun preferredStoredCategory(): String = storedCategory
        .takeIf { rememberLastCategory && it in HomeUiState.defaultCategories }
        ?: SpotCategories.CHARGING

    private fun filterSpots(spots: List<SpotDto>, category: String, searchText: String): List<SpotDto> =
        spots.filterByCategoryAndSearch(category, searchText)

    private inline fun updateSuccess(transform: (HomeUiState.Success) -> HomeUiState.Success) {
        _uiState.update { state ->
            if (state is HomeUiState.Success) transform(state) else state
        }
    }

    private fun rememberRecentSearch(label: String) {
        val trimmed = label.trim()
        if (trimmed.isBlank()) return
        recentSearchQueries.remove(trimmed)
        recentSearchQueries.add(0, trimmed)
        while (recentSearchQueries.size > 5) {
            recentSearchQueries.removeAt(recentSearchQueries.lastIndex)
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
