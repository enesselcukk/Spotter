package com.example.spotter.feature.home.domain.model

import kotlinx.serialization.Serializable

@Serializable
data class OverpassResponse(
    val elements: List<OsmElement> = emptyList(),
)

@Serializable
data class OsmElement(
    val type: String,
    val id: Long,
    val lat: Double? = null,
    val lon: Double? = null,
    val center: OsmCenter? = null,
    val tags: Map<String, String>? = null,
)

@Serializable
data class OsmCenter(
    val lat: Double,
    val lon: Double,
)
