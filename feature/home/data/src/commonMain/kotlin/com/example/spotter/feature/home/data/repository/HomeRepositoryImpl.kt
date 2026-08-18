package com.example.spotter.feature.home.data.repository

import com.example.spotter.core.data.BaseRepository
import com.example.spotter.core.domain.result.RestResult
import com.example.spotter.feature.home.data.mapper.toSpots
import com.example.spotter.feature.home.data.network.HomeApi
import com.example.spotter.feature.home.domain.model.HomePreloadResult
import com.example.spotter.feature.home.domain.model.OverpassResponse
import com.example.spotter.feature.home.domain.model.SpotDto
import com.example.spotter.feature.home.domain.model.SpotSearchQuery
import com.example.spotter.feature.home.domain.repository.HomePreloadRepository
import com.example.spotter.feature.home.domain.repository.HomeRepository
import com.example.spotter.feature.home.domain.util.GeoDistance
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlin.math.abs

class HomeRepositoryImpl(
    private val homeApi: HomeApi,
    private val homePreloadRepository: HomePreloadRepository,
) : BaseRepository(), HomeRepository {

    override fun getNearbySpots(query: SpotSearchQuery): Flow<RestResult<List<SpotDto>>> = flow {
        emit(RestResult.Loading())

        val cachedSpots = readCachedSpots(query)
        if (!cachedSpots.isNullOrEmpty()) {
            emit(RestResult.Loading(result = cachedSpots))
        }

        when (val networkResponse = safeApiCallWithRetry<OverpassResponse> { homeApi.getNearbySpots(query) }) {
            is RestResult.Success -> {
                val spots = withDistance(networkResponse.result.toSpots(), query)
                if (spots.isNotEmpty()) {
                    homePreloadRepository.save(
                        HomePreloadResult(
                            searchQuery = query,
                            spots = spots,
                        ),
                    )
                }
                emit(RestResult.Success(spots))
            }

            is RestResult.Error -> {
                if (!cachedSpots.isNullOrEmpty()) {
                    emit(RestResult.Success(cachedSpots))
                } else {
                    emit(RestResult.Error(networkResponse.error))
                }
            }

            is RestResult.Loading -> emit(RestResult.Loading())
        }
    }

    private fun readCachedSpots(query: SpotSearchQuery): List<SpotDto>? {
        val cached = homePreloadRepository.getFreshCache()
            ?: homePreloadRepository.getAnyCache()
            ?: return null

        if (cached.spots.isEmpty()) return null
        if (!queriesMatch(cached.searchQuery, query)) return null
        return withDistance(cached.spots, query)
    }

    private fun withDistance(spots: List<SpotDto>, query: SpotSearchQuery): List<SpotDto> =
        GeoDistance.withDistanceFrom(
            spots = spots,
            latitude = query.latitude,
            longitude = query.longitude,
        )

    private fun queriesMatch(cached: SpotSearchQuery, query: SpotSearchQuery): Boolean =
        abs(cached.latitude - query.latitude) < 0.02 &&
            abs(cached.longitude - query.longitude) < 0.02

}
