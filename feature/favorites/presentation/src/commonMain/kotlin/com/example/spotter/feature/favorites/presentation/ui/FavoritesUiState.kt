package com.example.spotter.feature.favorites.presentation.ui

import androidx.compose.runtime.Immutable
import com.example.spotter.core.spotui.SpotCategories
import com.example.spotter.core.spotui.buildSearchSuggestions
import com.example.spotter.core.spotui.filterByCategoryAndSearch
import com.example.spotter.feature.home.domain.model.SpotDto

@Immutable
data class FavoritesUiState(
    val favorites: List<SpotDto> = emptyList(),
    val categories: List<String> = SpotCategories.ids,
    val selectedCategory: String = SpotCategories.CHARGING,
    val searchText: String = "",
    val isSearchActive: Boolean = false,
    val recentSearchQueries: List<String> = emptyList(),
) {
    val filteredFavorites: List<SpotDto>
        get() = favorites.filterByCategoryAndSearch(selectedCategory, searchText)

    val searchSuggestions
        get() = buildSearchSuggestions(
            spots = favorites,
            query = searchText,
            recentQueries = recentSearchQueries,
            categories = categories,
        )
}
