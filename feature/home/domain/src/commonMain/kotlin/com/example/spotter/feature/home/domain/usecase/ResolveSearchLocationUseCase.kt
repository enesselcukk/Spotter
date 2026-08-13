package com.example.spotter.feature.home.domain.usecase

import com.example.spotter.feature.home.domain.model.SpotSearchQuery
import com.example.spotter.feature.home.domain.repository.LocationRepository

class ResolveSearchLocationUseCase(
    private val locationRepository: LocationRepository,
) {
    suspend operator fun invoke(): SpotSearchQuery {
        val deviceLocation = locationRepository.getDeviceLocation()
        return if (deviceLocation != null) {
            SpotSearchQuery(
                latitude = deviceLocation.latitude,
                longitude = deviceLocation.longitude,
                locationLabel = deviceLocation.label.orEmpty(),
                isDeviceLocation = true,
            )
        } else {
            SpotSearchQuery.fallback()
        }
    }
}
