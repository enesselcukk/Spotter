package com.example.spotter.feature.favorites.presentation.ui

import androidx.lifecycle.viewModelScope
import com.example.spotter.core.datastore.UserSettingsRepository
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
import com.example.spotter.feature.favorites.domain.usecase.ObserveFavoritesUseCase
import com.example.spotter.feature.favorites.domain.usecase.ToggleFavoriteUseCase
import com.example.spotter.feature.home.contract.HomeScreenDestination
import com.example.spotter.feature.home.domain.model.SpotDto
import com.example.spotter.feature.home.domain.model.SpotSearchQuery
import com.example.spotter.feature.map.contract.MapScreenDestination
import com.example.spotter.feature.map.domain.repository.MapSpotsHandoff
import com.example.spotter.feature.settings.contract.SettingsScreenDestination
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class FavoritesViewModel(
    observeFavoritesUseCase: ObserveFavoritesUseCase,
    private val toggleFavoriteUseCase: ToggleFavoriteUseCase,
    private val mapSpotsHandoff: MapSpotsHandoff,
    private val navigationManager: NavigationManager,
    userSettingsRepository: UserSettingsRepository,
) : CoreViewModel() {

    private val _uiState = MutableStateFlow(FavoritesUiState())
    val uiState: StateFlow<FavoritesUiState> = _uiState.asStateFlow()
    private val recentSearchQueries = mutableListOf<String>()

    init {
        viewModelScope.launch {
            userSettingsRepository.defaultHomeViewMode.collect { layoutMode ->
                _uiState.update { it.copy(listLayoutMode = layoutMode) }
            }
        }

        viewModelScope.launch {
            observeFavoritesUseCase().collect { favorites ->
                _uiState.update { current ->
                    current.copy(
                        favorites = favorites,
                        selectedCategory = resolveCategoryWithResults(
                            favorites = favorites,
                            preferred = current.selectedCategory,
                            searchText = current.searchText,
                        ),
                        recentSearchQueries = recentSearchQueries.toList(),
                    )
                }
            }
        }
    }

    fun onSearchTextChanged(query: String) {
        _uiState.update { current ->
            current.copy(searchText = query)
        }
    }

    fun onSearchFocusChanged(active: Boolean) {
        _uiState.update { it.copy(isSearchActive = active) }
    }

    fun onSearchClear() {
        _uiState.update { it.copy(searchText = "") }
    }

    fun onSearchSuggestionSelected(suggestion: SpotSearchSuggestion) {
        when (suggestion.kind) {
            SpotSearchSuggestionKind.Spot -> {
                val spot = _uiState.value.favorites.find { it.id == suggestion.spotId } ?: return
                rememberRecentSearch(suggestion.label)
                _uiState.update { current ->
                    current.copy(
                        searchText = suggestion.label,
                        selectedCategory = spot.amenity ?: current.selectedCategory,
                        isSearchActive = false,
                        recentSearchQueries = recentSearchQueries.toList(),
                    )
                }
            }

            SpotSearchSuggestionKind.Recent -> {
                _uiState.update { current ->
                    current.copy(
                        searchText = suggestion.label,
                        isSearchActive = false,
                    )
                }
            }

            SpotSearchSuggestionKind.Category -> {
                val category = suggestion.category ?: return
                rememberRecentSearch(suggestion.label)
                _uiState.update { current ->
                    current.copy(
                        searchText = suggestion.label,
                        selectedCategory = category,
                        isSearchActive = false,
                        recentSearchQueries = recentSearchQueries.toList(),
                    )
                }
            }
        }
    }

    fun onCategorySelected(category: String) {
        _uiState.update { it.copy(selectedCategory = category) }
    }

    fun onFavoriteToggle(spotId: Long) {
        val spot = _uiState.value.favorites.find { it.id == spotId } ?: return
        viewModelScope.launch { toggleFavoriteUseCase(spot) }
    }

    fun onNavigateToSpot(spotId: Long) {
        val favorites = _uiState.value.favorites
        if (favorites.isNotEmpty()) {
            mapSpotsHandoff.publish(favorites)
        }
        val fallback = SpotSearchQuery.fallback()
        navigationManager.navigate(
            NavigationCommand.NavigateTo(
                to = MapScreenDestination(
                    userLatitude = fallback.latitude,
                    userLongitude = fallback.longitude,
                    focusedSpotId = spotId,
                ),
            ),
        )
    }

    fun onTabSelected(tab: SpotterTab) {
        if (tab == SpotterTab.Favorites) return
        if (tab == SpotterTab.Map) {
            openMap()
            return
        }

        val target = when (tab) {
            SpotterTab.Search -> HomeScreenDestination
            SpotterTab.Map -> return
            SpotterTab.Favorites -> FavoritesScreenDestination
            SpotterTab.Settings -> SettingsScreenDestination
        }
        navigationManager.switchTab(
            target = target,
            root = HomeScreenDestination,
            currentIsRoot = false,
        )
    }

    private fun openMap() {
        val favorites = _uiState.value.favorites
        if (favorites.isNotEmpty()) {
            mapSpotsHandoff.publish(favorites)
        }
        val fallback = SpotSearchQuery.fallback()
        val focused = favorites.firstOrNull()
        navigationManager.navigate(
            NavigationCommand.NavigateTo(
                to = MapScreenDestination(
                    userLatitude = fallback.latitude,
                    userLongitude = fallback.longitude,
                    focusedSpotId = focused?.id,
                ),
            ),
        )
    }

    private fun resolveCategoryWithResults(
        favorites: List<SpotDto>,
        preferred: String,
        searchText: String,
    ): String {
        if (favorites.filterByCategoryAndSearch(preferred, searchText).isNotEmpty()) return preferred
        return SpotCategories.ids.firstOrNull { category ->
            favorites.filterByCategoryAndSearch(category, searchText).isNotEmpty()
        } ?: preferred
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
}
