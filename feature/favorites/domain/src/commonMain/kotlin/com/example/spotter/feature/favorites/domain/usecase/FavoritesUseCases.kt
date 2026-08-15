package com.example.spotter.feature.favorites.domain.usecase

import com.example.spotter.feature.favorites.domain.repository.FavoritesRepository
import com.example.spotter.feature.home.domain.model.SpotDto
import kotlinx.coroutines.flow.Flow

class ObserveFavoritesUseCase(
    private val favoritesRepository: FavoritesRepository,
) {
    operator fun invoke(): Flow<List<SpotDto>> = favoritesRepository.favorites
}

class ObserveFavoriteIdsUseCase(
    private val favoritesRepository: FavoritesRepository,
) {
    operator fun invoke(): Flow<Set<Long>> = favoritesRepository.favoriteIds
}

class ToggleFavoriteUseCase(
    private val favoritesRepository: FavoritesRepository,
) {
    suspend operator fun invoke(spot: SpotDto) = favoritesRepository.toggle(spot)
}
