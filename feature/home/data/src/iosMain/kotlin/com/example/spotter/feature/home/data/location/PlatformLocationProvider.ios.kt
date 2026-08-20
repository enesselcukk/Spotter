package com.example.spotter.feature.home.data.location

import com.example.spotter.core.location.LocationPlatform
import com.example.spotter.core.location.isGranted
import com.example.spotter.feature.home.domain.model.DeviceLocation

actual class PlatformLocationProvider {

    actual fun hasLocationPermission(): Boolean =
        LocationPlatform.permissionStatus().isGranted()

    actual suspend fun getDeviceLocation(): DeviceLocation? {
        if (!hasLocationPermission()) return null

        val coordinates = LocationPlatform.currentLocation() ?: return null
        val label = LocationPlatform.reverseGeocode(
            latitude = coordinates.latitude,
            longitude = coordinates.longitude,
        )

        return DeviceLocation(
            latitude = coordinates.latitude,
            longitude = coordinates.longitude,
            label = label,
        )
    }
}
