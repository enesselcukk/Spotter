package com.example.spotter.core.location

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.emptyFlow

actual object LocationPlatform {

    actual fun permissionStatus(): LocationPermissionStatus =
        LocationPermissionStatus.Denied

    actual suspend fun requestWhenInUsePermission(): LocationPermissionStatus =
        LocationPermissionStatus.Denied

    actual suspend fun currentLocation(timeoutMs: Long): GeoCoordinates? = null

    actual fun locationUpdates(): Flow<GeoCoordinates> = emptyFlow()

    actual suspend fun reverseGeocode(latitude: Double, longitude: Double): String? = null
}
