package com.example.spotter.feature.map.data.network.model

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class OsrmRouteResponse(
    val code: String = "",
    val message: String? = null,
    val routes: List<OsrmRoute> = emptyList(),
)

@Serializable
data class OsrmRoute(
    val geometry: String? = null,
    val distance: Double = 0.0,
    val duration: Double = 0.0,
    val legs: List<OsrmLeg> = emptyList(),
)

@Serializable
data class OsrmLeg(
    val distance: Double = 0.0,
    val duration: Double = 0.0,
    val summary: String? = null,
    val steps: List<OsrmStep> = emptyList(),
)

@Serializable
data class OsrmStep(
    val distance: Double = 0.0,
    val duration: Double = 0.0,
    val name: String = "",
    val maneuver: OsrmManeuver? = null,
)

@Serializable
data class OsrmManeuver(
    val type: String = "",
    val modifier: String? = null,
    @SerialName("location")
    val location: List<Double> = emptyList(),
)
