package com.example.spotter.feature.home.data.location

import com.example.spotter.core.location.IosLocationManager
import com.example.spotter.core.location.isGranted
import com.example.spotter.feature.home.domain.model.DeviceLocation

actual class PlatformLocationProvider {

    actual fun hasLocationPermission(): Boolean =
        IosLocationManager.currentPermissionStatus().isGranted()

    actual suspend fun getDeviceLocation(): DeviceLocation? {
        if (!hasLocationPermission()) return null

        val coordinates = IosLocationManager.currentLocation() ?: return null
        val label = IosLocationManager.reverseGeocode(
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
