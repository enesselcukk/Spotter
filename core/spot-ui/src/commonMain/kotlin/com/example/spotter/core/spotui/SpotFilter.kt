package com.example.spotter.core.spotui

import com.example.spotter.feature.home.domain.model.SpotDto

fun SpotDto.matchesSearch(query: String): Boolean {
    if (query.isBlank()) return true
    val needle = query.trim()
    return listOfNotNull(name, operator, socketSummary, amenity)
        .any { it.contains(needle, ignoreCase = true) }
}

fun List<SpotDto>.filterByCategoryAndSearch(
    category: String,
    searchText: String,
): List<SpotDto> = filter { spot ->
    spot.amenity == category && spot.matchesSearch(searchText)
}
