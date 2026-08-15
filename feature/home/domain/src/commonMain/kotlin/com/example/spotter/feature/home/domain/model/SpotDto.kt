package com.example.spotter.feature.home.domain.model

import kotlinx.serialization.Serializable

@Serializable
data class SpotDto(
    val id: Long,
    val name: String?,
    val amenity: String?,
    val lat: Double,
    val lon: Double,
    val operator: String? = null,
    val openingHours: String? = null,
    val socketSummary: String? = null,
    val distanceMeters: Double? = null,
)
