package com.example.spotter.feature.home.data.repository

import com.example.spotter.core.data.BaseRepository
import com.example.spotter.core.domain.result.RestResult
import com.example.spotter.feature.home.data.mapper.toSpots
import com.example.spotter.feature.home.data.network.HomeApi
import com.example.spotter.feature.home.domain.model.OverpassResponse
import com.example.spotter.feature.home.domain.model.SpotDto
import com.example.spotter.feature.home.domain.model.SpotSearchQuery
import com.example.spotter.feature.home.domain.repository.HomeRepository
import com.example.spotter.feature.home.domain.util.GeoDistance
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

class HomeRepositoryImpl(
    private val homeApi: HomeApi,
) : BaseRepository(), HomeRepository {

    override fun getNearbySpots(query: SpotSearchQuery): Flow<RestResult<List<SpotDto>>> =
        networkOnlyFlow(
            fetchFromNetwork = { homeApi.getNearbySpots(query) },
            mapToDomain = OverpassResponse::toSpots,
        ).map { result ->
            when (result) {
                is RestResult.Success -> RestResult.Success(
                    GeoDistance.withDistanceFrom(
                        spots = result.result,
                        latitude = query.latitude,
                        longitude = query.longitude,
                    ),
                )
                is RestResult.Error -> result.copy(
                    result = result.result?.let {
                        GeoDistance.withDistanceFrom(it, query.latitude, query.longitude)
                    },
                )
                is RestResult.Loading -> result.copy(
                    result = result.result?.let {
                        GeoDistance.withDistanceFrom(it, query.latitude, query.longitude)
                    },
                )
            }
        }
}
