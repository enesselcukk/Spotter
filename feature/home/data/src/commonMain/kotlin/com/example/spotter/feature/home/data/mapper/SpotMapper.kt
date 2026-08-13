package com.example.spotter.feature.home.data.mapper

import com.example.spotter.feature.home.domain.model.OsmElement
import com.example.spotter.feature.home.domain.model.OverpassResponse
import com.example.spotter.feature.home.domain.model.SpotCategory
import com.example.spotter.feature.home.domain.model.SpotDto

fun OverpassResponse.toSpots(): List<SpotDto> =
    elements
        .mapNotNull { it.toSpotDto() }
        .distinctBy { it.id }

private fun OsmElement.toSpotDto(): SpotDto? {
    val latitude = lat ?: center?.lat ?: return null
    val longitude = lon ?: center?.lon ?: return null
    val tags = tags.orEmpty()
    val category = tags.resolveSpotCategory() ?: return null

    return SpotDto(
        id = id,
        name = tags["name"],
        amenity = category,
        lat = latitude,
        lon = longitude,
        operator = tags["operator"] ?: tags["brand"],
        openingHours = tags["opening_hours"],
        socketSummary = tags.buildSocketSummary(),
    )
}

private fun Map<String, String>.resolveSpotCategory(): String? = when {
    get("amenity") in SUPPORTED_AMENITIES -> get("amenity")
    get("shop") == "car_repair" -> SpotCategory.CAR_REPAIR
    else -> null
}

private val SUPPORTED_AMENITIES = setOf(
    SpotCategory.CHARGING,
    SpotCategory.CAR_WASH,
    SpotCategory.PARKING,
    SpotCategory.FUEL,
)

private fun Map<String, String>.buildSocketSummary(): String? {
    val parts = buildList {
        get("socket:type2")?.let { add("Type 2 ($it kW)") }
        get("socket:schuko")?.let { add("Schuko") }
        get("socket:chademo")?.let { add("CHAdeMO ($it kW)") }
        get("socket:ccs")?.let { add("CCS ($it kW)") }
        get("socket:tesla_supercharger")?.let { add("Tesla Supercharger") }
    }
    return parts.takeIf { it.isNotEmpty() }?.joinToString(" • ")
}
