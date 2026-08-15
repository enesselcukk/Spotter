package com.example.spotter.feature.map.domain.usecase

import com.example.spotter.core.domain.result.RestResult
import com.example.spotter.feature.map.domain.model.RoutePlan
import com.example.spotter.feature.map.domain.model.RoutePoint
import com.example.spotter.feature.map.domain.model.TravelMode
import com.example.spotter.feature.map.domain.repository.RouteRepository
import kotlinx.coroutines.flow.Flow

class GetRoutePlanUseCase(
    private val routeRepository: RouteRepository,
) {
    operator fun invoke(
        origin: RoutePoint,
        destination: RoutePoint,
        travelMode: TravelMode = TravelMode.DRIVING,
    ): Flow<RestResult<RoutePlan>> = routeRepository.getRoute(origin, destination, travelMode)
}
