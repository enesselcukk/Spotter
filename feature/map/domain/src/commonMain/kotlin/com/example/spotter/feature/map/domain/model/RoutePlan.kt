package com.example.spotter.feature.map.domain.model

data class RoutePlan(
    val origin: RoutePoint,
    val destination: RoutePoint,
    val travelMode: TravelMode,
    val distanceMeters: Double,
    val durationSeconds: Double,
    val geometry: List<RoutePoint>,
    val steps: List<RouteStep>,
)
