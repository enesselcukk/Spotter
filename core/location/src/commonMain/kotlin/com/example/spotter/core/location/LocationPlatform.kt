package com.example.spotter.core.location

import kotlinx.coroutines.flow.Flow

expect object LocationPlatform {
    fun permissionStatus(): LocationPermissionStatus

    suspend fun requestWhenInUsePermission(): LocationPermissionStatus

    suspend fun currentLocation(timeoutMs: Long = DEFAULT_LOCATION_TIMEOUT_MS): GeoCoordinates?

    fun locationUpdates(): Flow<GeoCoordinates>

    suspend fun reverseGeocode(latitude: Double, longitude: Double): String?
}

internal const val DEFAULT_LOCATION_TIMEOUT_MS = 10_000L
