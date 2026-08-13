package com.example.spotter.feature.home.presentation.map

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import com.example.spotter.feature.home.domain.model.SpotDto

@Composable
actual fun SpotMapView(
    spots: List<SpotDto>,
    selectedSpotId: Long?,
    userLatitude: Double,
    userLongitude: Double,
    onSpotSelected: (Long) -> Unit,
    modifier: Modifier,
) {
    val colors = MaterialTheme.colorScheme

    Box(
        modifier = modifier
            .fillMaxSize()
            .background(colors.surfaceVariant),
        contentAlignment = Alignment.Center,
    ) {
        Text(
            text = "Map is available on Android with OSMDroid.",
            color = colors.onSurfaceVariant,
        )
    }
}
