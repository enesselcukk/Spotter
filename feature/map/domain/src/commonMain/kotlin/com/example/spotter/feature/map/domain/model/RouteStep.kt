package com.example.spotter.feature.map.domain.model

/**
 * Turn-by-turn instruction. [maneuver] and [modifier] follow the OSRM vocabulary
 * ("turn"/"left", "roundabout"/"right", ...) so presentation can localize them.
 */
data class RouteStep(
    val maneuver: String,
    val modifier: String?,
    val roadName: String,
    val distanceMeters: Double,
    val durationSeconds: Double,
    val location: RoutePoint?,
)
