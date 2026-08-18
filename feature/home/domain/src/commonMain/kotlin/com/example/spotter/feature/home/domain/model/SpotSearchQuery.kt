package com.example.spotter.feature.home.domain.model

import kotlinx.serialization.Serializable

@Serializable
data class SpotSearchQuery(
    val latitude: Double,
    val longitude: Double,
    val radiusMeters: Int = DEFAULT_RADIUS_METERS,
    val locationLabel: String = "",
    val isDeviceLocation: Boolean = false,
) {
    companion object {
        const val DEFAULT_RADIUS_METERS = 5_000

        private const val FALLBACK_LATITUDE = 41.0082
        private const val FALLBACK_LONGITUDE = 28.9784

        fun fallback(): SpotSearchQuery = SpotSearchQuery(
            latitude = FALLBACK_LATITUDE,
            longitude = FALLBACK_LONGITUDE,
        )
    }
}
