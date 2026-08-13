package com.example.spotter.feature.home.presentation.map

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import com.example.spotter.feature.home.domain.model.SpotDto

@Composable
expect fun SpotMapView(
    spots: List<SpotDto>,
    selectedSpotId: Long?,
    userLatitude: Double,
    userLongitude: Double,
    onSpotSelected: (Long) -> Unit,
    modifier: Modifier = Modifier,
)
