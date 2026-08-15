package com.example.spotter.feature.favorites.domain.repository

import com.example.spotter.feature.home.domain.model.SpotDto
import kotlinx.coroutines.flow.Flow

/**
 * Favourites are shared between the spot list, the map and the favourites tab, and they have to
 * survive a restart even when no search has been run yet — so the whole spot is stored, not just
 * its id.
 */
interface FavoritesRepository {
    val favorites: Flow<List<SpotDto>>
    val favoriteIds: Flow<Set<Long>>

    suspend fun toggle(spot: SpotDto)

    suspend fun remove(spotId: Long)
}
