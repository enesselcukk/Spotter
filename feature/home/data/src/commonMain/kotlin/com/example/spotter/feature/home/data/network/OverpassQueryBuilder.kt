package com.example.spotter.feature.home.data.network

import com.example.spotter.feature.home.domain.model.SpotSearchQuery

internal object OverpassQueryBuilder {
    private const val QUERY_TIMEOUT_SECONDS = 55
    private const val MAX_RESULTS = 150

    fun build(query: SpotSearchQuery): String {
        val around = "around:${query.radiusMeters},${query.latitude},${query.longitude}"
        return """
            [out:json][timeout:$QUERY_TIMEOUT_SECONDS];
            (
              nwr($around)["amenity"="charging_station"];
              nwr($around)["amenity"="car_wash"];
              nwr($around)["amenity"="parking"];
              nwr($around)["amenity"="fuel"];
              nwr($around)["shop"="car_repair"];
            );
            out center $MAX_RESULTS;
        """.trimIndent()
    }
}
