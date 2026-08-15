package com.example.spotter.feature.map.presentation.di

import com.example.spotter.core.navigation.NavGraphProvider
import com.example.spotter.feature.map.presentation.navigate.MapProvider
import com.example.spotter.feature.map.presentation.ui.MapViewModel
import org.koin.core.module.dsl.viewModelOf
import org.koin.core.qualifier.named
import org.koin.dsl.module

val mapPresentationModule = module {
    single<NavGraphProvider>(named("MapProvider")) {
        MapProvider()
    }

    viewModelOf(::MapViewModel)
}
