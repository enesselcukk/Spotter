package com.example.spotter.feature.map.domain.repository

import com.example.spotter.feature.map.domain.model.RoutePoint
import kotlinx.coroutines.flow.Flow

/**
 * Live device locations used while turn-by-turn navigation is running inside the map screen.
 */
interface NavigationLocationTracker {
    fun locations(): Flow<RoutePoint>
}
