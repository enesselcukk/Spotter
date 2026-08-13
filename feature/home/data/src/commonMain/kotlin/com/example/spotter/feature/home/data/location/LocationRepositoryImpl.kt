package com.example.spotter.feature.home.data.location

import com.example.spotter.feature.home.domain.model.DeviceLocation
import com.example.spotter.feature.home.domain.repository.LocationRepository

internal class LocationRepositoryImpl(
    private val platformLocationProvider: PlatformLocationProvider,
) : LocationRepository {

    override fun hasLocationPermission(): Boolean =
        platformLocationProvider.hasLocationPermission()

    override suspend fun getDeviceLocation(): DeviceLocation? =
        platformLocationProvider.getDeviceLocation()
}
