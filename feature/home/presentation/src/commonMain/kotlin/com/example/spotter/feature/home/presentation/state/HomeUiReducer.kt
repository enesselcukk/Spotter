package com.example.spotter.feature.home.presentation.state

import com.example.spotter.core.domain.result.RestResult
import com.example.spotter.core.spotui.SpotCategories
import com.example.spotter.core.spotui.SpotSearchSuggestion
import com.example.spotter.core.spotui.SpotSearchSuggestionKind
import com.example.spotter.core.spotui.component.SpotCardBounds
import com.example.spotter.core.spotui.filterByCategoryAndSearch
import com.example.spotter.feature.home.domain.model.SpotDto
import com.example.spotter.feature.home.domain.model.SpotSearchQuery
import com.example.spotter.feature.home.domain.util.SpotListSorter
import com.example.spotter.feature.map.domain.model.RoutePoint

internal object HomeUiReducer {
    const val EMPTY_SPOTS_MESSAGE = "No spots found nearby"
    private const val RECENT_SEARCH_LIMIT = 5

    fun success(
        spots: List<SpotDto>,
        searchQuery: SpotSearchQuery,
        preferences: HomeListPreferences,
        current: HomeUiState.Success?,
        isLoadingSpots: Boolean = false,
        preferredCategory: String? = null,
    ): HomeUiState.Success {
        val sortedSpots = SpotListSorter.sort(spots, preferences.sortOrder)
        val category = resolveCategoryWithResults(
            spots = sortedSpots,
            preferred = preferredCategory ?: current?.selectedCategory ?: preferences.preferredCategory,
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
            recentSearchQueries = current?.recentSearchQueries.orEmpty(),
            listLayoutMode = preferences.defaultViewMode,
            expandedSpotId = current?.expandedSpotId,
            expandedSpotSourceBounds = current?.expandedSpotSourceBounds,
            isExpandedSpotVisible = current?.isExpandedSpotVisible ?: false,
            expandedRouteGeometry = current?.expandedRouteGeometry.orEmpty(),
            isExpandedRouteLoading = current?.isExpandedRouteLoading ?: false,
        )
    }

    fun fromSpotResult(
        result: RestResult<List<SpotDto>>,
        searchQuery: SpotSearchQuery,
        preferences: HomeListPreferences,
        current: HomeUiState.Success?,
    ): HomeUiState = when (result) {
        is RestResult.Loading -> result.result?.let {
            success(it, searchQuery, preferences, current, isLoadingSpots = true)
        } ?: current?.copy(isLoadingSpots = true) ?: HomeUiState.Loading

        is RestResult.Success -> if (result.result.isEmpty()) {
            HomeUiState.Error(message = EMPTY_SPOTS_MESSAGE)
        } else {
            success(result.result, searchQuery, preferences, current)
        }

        is RestResult.Error -> result.result?.let {
            success(it, searchQuery, preferences, current)
        } ?: HomeUiState.Error(message = result.error.message)
    }

    fun selectCategory(state: HomeUiState.Success, category: String): HomeUiState.Success =
        state.copy(
            selectedCategory = category,
            selectedSpotId = filterSpots(state.spots, category, state.searchText).firstOrNull()?.id,
        )

    fun searchTextChanged(state: HomeUiState.Success, query: String): HomeUiState.Success =
        state.copy(
            searchText = query,
            selectedSpotId = filterSpots(state.spots, state.selectedCategory, query).firstOrNull()?.id,
        )

    fun applySearchSuggestion(
        state: HomeUiState.Success,
        suggestion: SpotSearchSuggestion,
    ): HomeUiState.Success? = when (suggestion.kind) {
        SpotSearchSuggestionKind.Spot -> {
            val spot = state.spots.find { it.id == suggestion.spotId } ?: return null
            state.copy(
                searchText = suggestion.label,
                selectedCategory = spot.amenity ?: state.selectedCategory,
                selectedSpotId = spot.id,
                isSearchActive = false,
                recentSearchQueries = rememberRecentSearch(state.recentSearchQueries, suggestion.label),
            )
        }

        SpotSearchSuggestionKind.Recent -> state.copy(
            searchText = suggestion.label,
            isSearchActive = false,
            selectedSpotId = filterSpots(
                state.spots,
                state.selectedCategory,
                suggestion.label,
            ).firstOrNull()?.id,
        )

        SpotSearchSuggestionKind.Category -> {
            val category = suggestion.category ?: return null
            state.copy(
                searchText = suggestion.label,
                selectedCategory = category,
                selectedSpotId = filterSpots(state.spots, category, suggestion.label).firstOrNull()?.id,
                isSearchActive = false,
                recentSearchQueries = rememberRecentSearch(state.recentSearchQueries, suggestion.label),
            )
        }
    }

    fun expandSpot(
        state: HomeUiState.Success,
        spotId: Long,
        sourceBounds: SpotCardBounds,
    ): HomeUiState.Success = state.copy(
        expandedSpotId = spotId,
        expandedSpotSourceBounds = sourceBounds,
        isExpandedSpotVisible = true,
        selectedSpotId = spotId,
        expandedRouteGeometry = emptyList(),
        isExpandedRouteLoading = true,
    )

    fun hideExpandedSpot(state: HomeUiState.Success): HomeUiState.Success =
        state.copy(isExpandedSpotVisible = false)

    fun clearExpandedSpot(state: HomeUiState.Success): HomeUiState.Success = state.copy(
        expandedSpotId = null,
        expandedSpotSourceBounds = null,
        expandedRouteGeometry = emptyList(),
        isExpandedRouteLoading = false,
    )

    fun dismissExpandedSpotForNavigation(state: HomeUiState.Success): HomeUiState.Success =
        clearExpandedSpot(state).copy(isExpandedSpotVisible = false)

    fun routeLoaded(
        state: HomeUiState.Success,
        geometry: List<RoutePoint>,
    ): HomeUiState.Success = state.copy(
        expandedRouteGeometry = geometry,
        isExpandedRouteLoading = false,
    )

    private fun rememberRecentSearch(queries: List<String>, label: String): List<String> {
        val trimmed = label.trim()
        if (trimmed.isBlank()) return queries
        return (listOf(trimmed) + queries.filter { it != trimmed }).take(RECENT_SEARCH_LIMIT)
    }

    private fun resolveCategoryWithResults(
        spots: List<SpotDto>,
        preferred: String,
        searchText: String,
    ): String {
        if (filterSpots(spots, preferred, searchText).isNotEmpty()) return preferred
        return SpotCategories.ids.firstOrNull { filterSpots(spots, it, searchText).isNotEmpty() } ?: preferred
    }

    private fun filterSpots(spots: List<SpotDto>, category: String, searchText: String): List<SpotDto> =
        spots.filterByCategoryAndSearch(category, searchText)
}
