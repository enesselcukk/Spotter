package com.example.spotter.feature.home.data.location

import com.example.spotter.feature.home.domain.model.DeviceLocation

actual class PlatformLocationProvider {
    actual fun hasLocationPermission(): Boolean = false

    actual suspend fun getDeviceLocation(): DeviceLocation? = null
}
