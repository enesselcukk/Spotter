package com.example.spotter.feature.home.domain.usecase

import com.example.spotter.core.domain.result.RestResult
import com.example.spotter.feature.home.domain.model.SpotDto
import com.example.spotter.feature.home.domain.model.SpotSearchQuery
import com.example.spotter.feature.home.domain.repository.HomeRepository
import kotlinx.coroutines.flow.Flow

class GetNearbySpotsUseCase(
    private val homeRepository: HomeRepository,
) {
    operator fun invoke(
        query: SpotSearchQuery = SpotSearchQuery.fallback(),
    ): Flow<RestResult<List<SpotDto>>> = homeRepository.getNearbySpots(query)
}
