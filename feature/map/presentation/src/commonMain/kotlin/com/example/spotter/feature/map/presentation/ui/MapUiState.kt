package com.example.spotter.feature.map.presentation.ui

import androidx.compose.runtime.Immutable
import com.example.spotter.core.datastore.DistanceUnit
import com.example.spotter.feature.home.domain.model.SpotDto
import com.example.spotter.feature.map.domain.model.RoutePlan
import com.example.spotter.feature.map.domain.model.RoutePoint
import com.example.spotter.feature.map.domain.model.TravelMode

@Immutable
data class MapUiState(
    val userLocation: RoutePoint = RoutePoint(0.0, 0.0),
    val spots: List<SpotDto> = emptyList(),
    val selectedSpotId: Long? = null,
    val travelMode: TravelMode = TravelMode.DRIVING,
    val routePlan: RoutePlan? = null,
    val isRouteLoading: Boolean = false,
    val hasRouteError: Boolean = false,
    val areStepsExpanded: Boolean = false,
    val distanceUnit: DistanceUnit = DistanceUnit.KILOMETERS,
) {
    val selectedSpot: SpotDto?
        get() = spots.find { it.id == selectedSpotId }

    val routeGeometry: List<RoutePoint>
        get() = routePlan?.geometry.orEmpty()
}
