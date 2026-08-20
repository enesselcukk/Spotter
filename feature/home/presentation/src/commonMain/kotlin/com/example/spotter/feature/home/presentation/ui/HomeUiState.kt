package com.example.spotter.feature.home.presentation.ui

import androidx.compose.runtime.Immutable
import com.example.spotter.core.datastore.DefaultHomeViewMode
import com.example.spotter.core.spotui.SpotCategories
import com.example.spotter.core.spotui.buildSearchSuggestions
import com.example.spotter.core.spotui.filterByCategoryAndSearch
import com.example.spotter.core.spotui.component.SpotCardBounds
import com.example.spotter.feature.home.domain.model.SpotDto
import com.example.spotter.feature.map.domain.model.RoutePoint

@Immutable
sealed interface HomeUiState {
    data object Loading : HomeUiState

    data class Success(
        val spots: List<SpotDto>,
        val categories: List<String>,
        val selectedCategory: String,
        val selectedSpotId: Long?,
        val favoriteIds: Set<Long>,
        val locationLabel: String,
        val usesDeviceLocation: Boolean,
        val userLatitude: Double,
        val userLongitude: Double,
        val isLoadingSpots: Boolean = false,
        val searchText: String = "",
        val isSearchActive: Boolean = false,
        val recentSearchQueries: List<String> = emptyList(),
        val listLayoutMode: DefaultHomeViewMode = DefaultHomeViewMode.LIST,
        val expandedSpotId: Long? = null,
        val expandedSpotSourceBounds: SpotCardBounds? = null,
        val isExpandedSpotVisible: Boolean = false,
        val expandedRouteGeometry: List<RoutePoint> = emptyList(),
        val isExpandedRouteLoading: Boolean = false,
    ) : HomeUiState {
        val filteredSpots: List<SpotDto>
            get() = spots.filterByCategoryAndSearch(selectedCategory, searchText)

        val searchSuggestions
            get() = buildSearchSuggestions(
                spots = spots,
                query = searchText,
                recentQueries = recentSearchQueries,
                categories = categories,
            )
    }

    data class Error(val message: String? = null) : HomeUiState

    companion object {
        val defaultCategories = SpotCategories.ids
    }
}
