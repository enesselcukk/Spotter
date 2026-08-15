package com.example.spotter.feature.map.domain.util

import kotlin.math.abs
import kotlin.math.roundToInt

object RouteFormatter {

    fun formatDuration(
        seconds: Double,
        secondsSuffix: String,
        minutesSuffix: String,
        hoursSuffix: String,
    ): String {
        val totalMinutes = (seconds / 60).roundToInt()
        return when {
            totalMinutes < 1 -> "${seconds.roundToInt()} $secondsSuffix"
            totalMinutes < 60 -> "$totalMinutes $minutesSuffix"
            else -> {
                val hours = totalMinutes / 60
                val minutes = totalMinutes % 60
                if (minutes == 0) "$hours $hoursSuffix" else "$hours $hoursSuffix $minutes $minutesSuffix"
            }
        }
    }

    fun formatCoordinates(latitude: Double, longitude: Double): String {
        val latHemisphere = if (latitude >= 0) "N" else "S"
        val lonHemisphere = if (longitude >= 0) "E" else "W"
        return "${formatDegrees(abs(latitude))}° $latHemisphere, ${formatDegrees(abs(longitude))}° $lonHemisphere"
    }

    private fun formatDegrees(value: Double): String {
        val scaled = (value * 100_000).roundToInt()
        val whole = scaled / 100_000
        val fraction = scaled % 100_000
        return "$whole.${fraction.toString().padStart(5, '0')}"
    }
}
