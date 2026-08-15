package com.example.spotter.feature.map.domain.repository

import com.example.spotter.core.domain.result.RestResult
import com.example.spotter.feature.map.domain.model.RoutePlan
import com.example.spotter.feature.map.domain.model.RoutePoint
import com.example.spotter.feature.map.domain.model.TravelMode
import kotlinx.coroutines.flow.Flow

interface RouteRepository {
    fun getRoute(
        origin: RoutePoint,
        destination: RoutePoint,
        travelMode: TravelMode,
    ): Flow<RestResult<RoutePlan>>
}
