package com.example.spotter.core.location

import kotlinx.coroutines.flow.Flow

actual object LocationPlatform {

    actual fun permissionStatus(): LocationPermissionStatus =
        IosLocationManager.currentPermissionStatus()

    actual suspend fun requestWhenInUsePermission(): LocationPermissionStatus =
        IosLocationManager.requestWhenInUsePermission()

    actual suspend fun currentLocation(timeoutMs: Long): GeoCoordinates? =
        IosLocationManager.currentLocation(timeoutMs = timeoutMs)

    actual fun locationUpdates(): Flow<GeoCoordinates> =
        IosLocationManager.locationUpdates()

    actual suspend fun reverseGeocode(latitude: Double, longitude: Double): String? =
        IosLocationManager.reverseGeocode(latitude = latitude, longitude = longitude)
}
