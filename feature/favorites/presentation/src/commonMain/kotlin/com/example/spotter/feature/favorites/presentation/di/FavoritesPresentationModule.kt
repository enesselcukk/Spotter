package com.example.spotter.feature.favorites.presentation.di

import com.example.spotter.core.navigation.NavGraphProvider
import com.example.spotter.feature.favorites.presentation.navigate.FavoritesProvider
import com.example.spotter.feature.favorites.presentation.ui.FavoritesViewModel
import org.koin.core.module.dsl.viewModelOf
import org.koin.core.qualifier.named
import org.koin.dsl.module

val favoritesPresentationModule = module {
    single<NavGraphProvider>(named("FavoritesProvider")) {
        FavoritesProvider()
    }

    viewModelOf(::FavoritesViewModel)
}
