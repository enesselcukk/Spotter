package com.example.spotter.feature.map.data.repository

import com.example.spotter.core.data.BaseRepository
import com.example.spotter.core.domain.result.RestResult
import com.example.spotter.feature.map.data.mapper.toRoutePlan
import com.example.spotter.feature.map.data.network.RoutingApi
import com.example.spotter.feature.map.data.network.model.OsrmRouteResponse
import com.example.spotter.feature.map.domain.model.RoutePlan
import com.example.spotter.feature.map.domain.model.RoutePoint
import com.example.spotter.feature.map.domain.model.TravelMode
import com.example.spotter.feature.map.domain.repository.RouteRepository
import kotlinx.coroutines.flow.Flow

class RouteRepositoryImpl(
    private val routingApi: RoutingApi,
) : BaseRepository(), RouteRepository {

    override fun getRoute(
        origin: RoutePoint,
        destination: RoutePoint,
        travelMode: TravelMode,
    ): Flow<RestResult<RoutePlan>> = networkOnlyFlow(
        fetchFromNetwork = { routingApi.getRoute(origin, destination, travelMode) },
        mapToDomain = { response: OsrmRouteResponse ->
            response.toRoutePlan(origin, destination, travelMode)
        },
    )
}
