package com.example.spotter.feature.favorites.domain.repository

import com.example.spotter.feature.home.domain.model.SpotDto
import kotlinx.coroutines.flow.Flow

interface FavoritesRepository {
    val favorites: Flow<List<SpotDto>>
    val favoriteIds: Flow<Set<Long>>

    suspend fun toggle(spot: SpotDto)

    suspend fun remove(spotId: Long)
}
