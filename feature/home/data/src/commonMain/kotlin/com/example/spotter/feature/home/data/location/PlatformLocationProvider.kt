package com.example.spotter.feature.home.data.location

import com.example.spotter.feature.home.domain.model.DeviceLocation

expect class PlatformLocationProvider {
    fun hasLocationPermission(): Boolean
    suspend fun getDeviceLocation(): DeviceLocation?
}
