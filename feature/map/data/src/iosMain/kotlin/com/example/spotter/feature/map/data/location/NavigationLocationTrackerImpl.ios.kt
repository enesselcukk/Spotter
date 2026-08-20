package com.example.spotter.feature.map.data.location

import com.example.spotter.core.location.IosLocationManager
import com.example.spotter.feature.map.domain.model.RoutePoint
import com.example.spotter.feature.map.domain.repository.NavigationLocationTracker
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

actual class NavigationLocationTrackerImpl : NavigationLocationTracker {

    actual override fun locations(): Flow<RoutePoint> =
        IosLocationManager.locationUpdates().map { coordinates ->
            RoutePoint(
                latitude = coordinates.latitude,
                longitude = coordinates.longitude,
            )
        }
}
