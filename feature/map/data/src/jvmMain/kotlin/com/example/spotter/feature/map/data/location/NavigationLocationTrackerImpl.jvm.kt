package com.example.spotter.feature.map.data.location

import com.example.spotter.feature.map.domain.model.RoutePoint
import com.example.spotter.feature.map.domain.repository.NavigationLocationTracker
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.emptyFlow

actual class NavigationLocationTrackerImpl : NavigationLocationTracker {
    actual override fun locations(): Flow<RoutePoint> = emptyFlow()
}
