package com.example.spotter.feature.home.presentation.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.LoadingIndicator
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import com.example.spotter.core.designsystem.theme.SpotterBlue
import com.example.spotter.feature.home.domain.model.SpotDto
import com.example.spotter.feature.map.domain.model.RoutePoint
import com.example.spotter.feature.map.presentation.map.MapCamera
import com.example.spotter.feature.map.presentation.map.RouteMapView
import com.example.spotter.feature.map.presentation.map.rememberRouteMapState

@Composable
fun SpotExpandedMapPreview(
    spot: SpotDto,
    userLatitude: Double,
    userLongitude: Double,
    routeGeometry: List<RoutePoint>,
    isRouteLoading: Boolean,
    modifier: Modifier = Modifier,
) {
    val mapState = rememberRouteMapState()
    val fallbackRoute = listOf(
        RoutePoint(userLatitude, userLongitude),
        RoutePoint(spot.lat, spot.lon),
    )
    val geometry = routeGeometry.takeIf { it.size > 1 } ?: fallbackRoute

    LaunchedEffect(geometry) {
        if (geometry.size > 1) {
            mapState.send(MapCamera.FitRoute)
        }
    }

    Box(modifier = modifier) {
        RouteMapView(
            userLocation = RoutePoint(userLatitude, userLongitude),
            spots = listOf(spot),
            selectedSpotId = spot.id,
            routeGeometry = geometry,
            mapState = mapState,
            onSpotSelected = {},
            interactive = false,
            modifier = Modifier.fillMaxSize(),
        )

        if (isRouteLoading) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(Color.Black.copy(alpha = 0.18f)),
                contentAlignment = Alignment.Center,
            ) {
                LoadingIndicator(color = SpotterBlue)
            }
        }
    }
}
