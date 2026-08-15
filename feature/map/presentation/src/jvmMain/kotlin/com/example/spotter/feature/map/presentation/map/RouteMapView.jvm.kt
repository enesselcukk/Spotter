package com.example.spotter.feature.map.presentation.map

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import com.example.spotter.feature.home.domain.model.SpotDto
import com.example.spotter.feature.map.domain.model.RoutePoint

@Composable
actual fun RouteMapView(
    userLocation: RoutePoint,
    spots: List<SpotDto>,
    selectedSpotId: Long?,
    routeGeometry: List<RoutePoint>,
    mapState: RouteMapState,
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
            text = "Map rendering is available on Android and iOS.",
            color = colors.onSurfaceVariant,
        )
    }
}
