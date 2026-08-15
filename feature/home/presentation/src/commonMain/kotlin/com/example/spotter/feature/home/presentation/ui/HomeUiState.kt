package com.example.spotter.feature.home.presentation.ui

import androidx.compose.runtime.Immutable
import com.example.spotter.core.spotui.SpotCategories
import com.example.spotter.feature.home.domain.model.SpotDto

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
    ) : HomeUiState {
        val filteredSpots: List<SpotDto>
            get() = spots.filter { it.amenity == selectedCategory }
    }

    data class Error(val message: String? = null) : HomeUiState

    companion object {
        val defaultCategories = SpotCategories.ids
    }
}
