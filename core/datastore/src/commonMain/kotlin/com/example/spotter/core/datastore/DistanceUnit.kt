package com.example.spotter.core.datastore

enum class DistanceUnit {
    KILOMETERS,
    MILES,
}

fun distanceUnitFromStorage(value: String?): DistanceUnit =
    when (value) {
        "MILES" -> DistanceUnit.MILES
        else -> DistanceUnit.KILOMETERS
    }
