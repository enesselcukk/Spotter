package com.example.spotter.feature.map.data.mapper

import com.example.spotter.feature.map.data.network.model.OsrmRouteResponse
import com.example.spotter.feature.map.domain.model.RoutePlan
import com.example.spotter.feature.map.domain.model.RoutePoint
import com.example.spotter.feature.map.domain.model.RouteStep
import com.example.spotter.feature.map.domain.model.TravelMode
import com.example.spotter.feature.map.domain.util.PolylineCodec

fun OsrmRouteResponse.toRoutePlan(
    origin: RoutePoint,
    destination: RoutePoint,
    travelMode: TravelMode,
): RoutePlan {
    val route = routes.firstOrNull()
    val geometry = route?.geometry?.let(PolylineCodec::decode).orEmpty()

    return RoutePlan(
        origin = origin,
        destination = destination,
        travelMode = travelMode,
        distanceMeters = route?.distance ?: 0.0,
        durationSeconds = route?.duration ?: 0.0,
        geometry = geometry.ifEmpty { listOf(origin, destination) },
        steps = route?.legs.orEmpty().flatMap { leg ->
            leg.steps.map { step ->
                RouteStep(
                    maneuver = step.maneuver?.type.orEmpty(),
                    modifier = step.maneuver?.modifier,
                    roadName = step.name,
                    distanceMeters = step.distance,
                    durationSeconds = step.duration,
                    location = step.maneuver?.location
                        ?.takeIf { it.size >= 2 }
                        ?.let { RoutePoint(latitude = it[1], longitude = it[0]) },
                )
            }
        },
    )
}
