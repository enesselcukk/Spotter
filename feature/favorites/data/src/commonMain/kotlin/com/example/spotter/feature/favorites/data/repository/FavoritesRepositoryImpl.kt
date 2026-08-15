package com.example.spotter.feature.favorites.data.repository

import com.example.spotter.feature.favorites.domain.repository.FavoritesRepository
import com.example.spotter.feature.home.domain.model.SpotDto
import com.russhwolf.settings.Settings
import com.russhwolf.settings.set
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.map
import kotlinx.serialization.builtins.ListSerializer
import kotlinx.serialization.json.Json

private const val FAVORITE_SPOTS_KEY = "favorite_spots"

class FavoritesRepositoryImpl(
    private val settings: Settings = Settings(),
    private val json: Json = Json { ignoreUnknownKeys = true },
) : FavoritesRepository {

    private val state = MutableStateFlow(read())

    override val favorites: Flow<List<SpotDto>> = state.asStateFlow()

    override val favoriteIds: Flow<Set<Long>> = state.map { spots -> spots.mapTo(mutableSetOf()) { it.id } }

    override suspend fun toggle(spot: SpotDto) {
        val current = state.value
        val updated = if (current.any { it.id == spot.id }) {
            current.filterNot { it.id == spot.id }
        } else {
            current + spot
        }
        persist(updated)
    }

    override suspend fun remove(spotId: Long) {
        persist(state.value.filterNot { it.id == spotId })
    }

    private fun persist(spots: List<SpotDto>) {
        settings[FAVORITE_SPOTS_KEY] = json.encodeToString(serializer, spots)
        state.value = spots
    }

    private fun read(): List<SpotDto> {
        val stored = settings.getStringOrNull(FAVORITE_SPOTS_KEY) ?: return emptyList()
        return runCatching { json.decodeFromString(serializer, stored) }.getOrDefault(emptyList())
    }

    private companion object {
        val serializer = ListSerializer(SpotDto.serializer())
    }
}
