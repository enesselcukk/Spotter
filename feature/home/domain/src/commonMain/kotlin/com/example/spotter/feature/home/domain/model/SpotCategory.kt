package com.example.spotter.feature.home.domain.model

object SpotCategory {
    const val CHARGING = "charging_station"
    const val CAR_WASH = "car_wash"
    const val PARKING = "parking"
    const val FUEL = "fuel"
    const val CAR_REPAIR = "car_repair"

    val ALL = listOf(CHARGING, CAR_WASH, PARKING, FUEL, CAR_REPAIR)
}
