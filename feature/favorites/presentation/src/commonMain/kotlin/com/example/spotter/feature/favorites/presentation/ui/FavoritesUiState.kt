package com.example.spotter.feature.favorites.presentation.ui

import androidx.compose.runtime.Immutable
import com.example.spotter.feature.home.domain.model.SpotDto

@Immutable
data class FavoritesUiState(
    val favorites: List<SpotDto> = emptyList(),
)
