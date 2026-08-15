package com.example.spotter.feature.favorites.data.di

import com.example.spotter.feature.favorites.data.repository.FavoritesRepositoryImpl
import com.example.spotter.feature.favorites.domain.repository.FavoritesRepository
import org.koin.core.module.Module
import org.koin.dsl.module

val favoritesDataModule: Module = module {
    single<FavoritesRepository> { FavoritesRepositoryImpl() }
}
