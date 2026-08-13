package com.example.spotter.feature.home.domain.repository

import com.example.spotter.feature.home.domain.model.DeviceLocation

interface LocationRepository {
    fun hasLocationPermission(): Boolean
    suspend fun getDeviceLocation(): DeviceLocation?
}
