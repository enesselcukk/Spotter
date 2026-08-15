package com.example.spotter.feature.favorites.domain.di

import com.example.spotter.feature.favorites.domain.usecase.ObserveFavoriteIdsUseCase
import com.example.spotter.feature.favorites.domain.usecase.ObserveFavoritesUseCase
import com.example.spotter.feature.favorites.domain.usecase.ToggleFavoriteUseCase
import org.koin.core.module.dsl.factoryOf
import org.koin.dsl.module

val favoritesDomainModule = module {
    factoryOf(::ObserveFavoritesUseCase)
    factoryOf(::ObserveFavoriteIdsUseCase)
    factoryOf(::ToggleFavoriteUseCase)
}
