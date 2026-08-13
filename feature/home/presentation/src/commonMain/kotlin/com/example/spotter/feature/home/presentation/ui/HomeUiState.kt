package com.example.spotter.feature.home.presentation.ui

import androidx.compose.runtime.Immutable
import com.example.spotter.feature.home.domain.model.SpotDto

@Immutable
sealed interface HomeUiState {
    data object Loading : HomeUiState

    data class Success(
        val spots: List<SpotDto>,
        val categories: List<String>,
        val selectedCategory: String,
        val viewMode: HomeViewMode,
        val selectedSpotId: Long?,
        val favoriteIds: Set<Long>,
        val locationLabel: String,
        val usesDeviceLocation: Boolean,
        val userLatitude: Double,
        val userLongitude: Double,
        val selectedBottomNav: HomeBottomNav,
        val isLoadingSpots: Boolean = false,
    ) : HomeUiState {
        val filteredSpots: List<SpotDto>
            get() = if (selectedCategory == HomeCategories.ALL) {
                spots
            } else {
                spots.filter { it.amenity == selectedCategory }
            }

        val favoriteSpots: List<SpotDto>
            get() = spots.filter { it.id in favoriteIds }

        val selectedSpot: SpotDto?
            get() = filteredSpots.find { it.id == selectedSpotId }
                ?: filteredSpots.firstOrNull()
    }

    data class Error(val message: String? = null) : HomeUiState

    companion object {
        val defaultCategories = HomeCategories.ids
    }
}
