package com.example.spotter.feature.map.domain.model

data class RouteStep(
    val maneuver: String,
    val modifier: String?,
    val roadName: String,
    val distanceMeters: Double,
    val durationSeconds: Double,
    val location: RoutePoint?,
)
