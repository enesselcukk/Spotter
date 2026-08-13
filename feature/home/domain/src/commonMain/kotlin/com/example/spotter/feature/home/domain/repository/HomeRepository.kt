package com.example.spotter.feature.home.domain.repository

import com.example.spotter.core.domain.result.RestResult
import com.example.spotter.feature.home.domain.model.SpotDto
import com.example.spotter.feature.home.domain.model.SpotSearchQuery
import kotlinx.coroutines.flow.Flow

interface HomeRepository {
    fun getNearbySpots(query: SpotSearchQuery = SpotSearchQuery.fallback()): Flow<RestResult<List<SpotDto>>>
}
