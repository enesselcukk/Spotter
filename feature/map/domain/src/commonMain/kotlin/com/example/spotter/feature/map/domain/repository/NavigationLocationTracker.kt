package com.example.spotter.feature.map.domain.repository

import com.example.spotter.feature.map.domain.model.RoutePoint
import kotlinx.coroutines.flow.Flow

interface NavigationLocationTracker {
    fun locations(): Flow<RoutePoint>
}
