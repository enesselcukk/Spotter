package com.example.spotter.core.spotui

import com.example.spotter.feature.home.domain.model.SpotCategory

object SpotCategories {
    const val ALL = "all"
    const val CHARGING = SpotCategory.CHARGING
    const val CAR_WASH = SpotCategory.CAR_WASH
    const val PARKING = SpotCategory.PARKING
    const val FUEL = SpotCategory.FUEL
    const val CAR_REPAIR = SpotCategory.CAR_REPAIR

    val ids = SpotCategory.ALL

    fun icon(category: String): String = when (category) {
        CHARGING -> "⚡"
        CAR_WASH -> "🚗"
        PARKING -> "🅿️"
        FUEL -> "⛽"
        CAR_REPAIR -> "🔧"
        else -> "📍"
    }
}
