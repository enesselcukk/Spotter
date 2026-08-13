package com.example.spotter.feature.home.domain.util

import com.example.spotter.core.datastore.DistanceUnit
import com.example.spotter.feature.home.domain.model.SpotDto
import kotlin.math.PI
import kotlin.math.atan2
import kotlin.math.cos
import kotlin.math.pow
import kotlin.math.sin
import kotlin.math.sqrt

private fun Double.toRadians(): Double = this * PI / 180.0

object GeoDistance {
    private const val EARTH_RADIUS_METERS = 6_371_000.0
    private const val METERS_IN_MILE = 1_609.344
    private const val METERS_IN_FOOT = 0.3048

    fun metersBetween(
        lat1: Double,
        lon1: Double,
        lat2: Double,
        lon2: Double,
    ): Double {
        val dLat = (lat2 - lat1).toRadians()
        val dLon = (lon2 - lon1).toRadians()
        val a = sin(dLat / 2).pow(2) +
            cos(lat1.toRadians()) * cos(lat2.toRadians()) * sin(dLon / 2).pow(2)
        return EARTH_RADIUS_METERS * 2 * atan2(sqrt(a), sqrt(1 - a))
    }

    fun formatDistance(
        meters: Double,
        unit: DistanceUnit = DistanceUnit.KILOMETERS,
    ): String = when (unit) {
        DistanceUnit.KILOMETERS -> formatMetric(meters)
        DistanceUnit.MILES -> formatImperial(meters)
    }

    private fun formatMetric(meters: Double): String =
        if (meters < 1_000) {
            "${meters.toInt()} m"
        } else {
            val km = meters / 1_000
            val rounded = (km * 10).toInt() / 10.0
            "$rounded km"
        }

    private fun formatImperial(meters: Double): String {
        val miles = meters / METERS_IN_MILE
        return if (miles < 0.1) {
            "${(meters / METERS_IN_FOOT).toInt()} ft"
        } else {
            val rounded = (miles * 10).toInt() / 10.0
            "$rounded mi"
        }
    }

    fun withDistanceFrom(
        spots: List<SpotDto>,
        latitude: Double,
        longitude: Double,
    ): List<SpotDto> = spots
        .map { spot ->
            spot.copy(
                distanceMeters = metersBetween(latitude, longitude, spot.lat, spot.lon),
            )
        }
        .sortedBy { it.distanceMeters }
}
