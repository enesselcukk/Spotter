package com.example.spotter.shared.di

import com.example.spotter.core.navigation.navigationModule
import com.example.spotter.core.network.di.overpassApiModule
import com.example.spotter.feature.favorites.data.di.favoritesDataModule
import com.example.spotter.feature.favorites.domain.di.favoritesDomainModule
import com.example.spotter.feature.favorites.presentation.di.favoritesPresentationModule
import com.example.spotter.feature.home.data.di.homeDataModule
import com.example.spotter.feature.home.domain.di.homeDomainModule
import com.example.spotter.feature.home.presentation.di.homePresentationModule
import com.example.spotter.feature.map.data.di.mapDataModule
import com.example.spotter.feature.map.domain.di.mapDomainModule
import com.example.spotter.feature.map.presentation.di.mapPresentationModule
import com.example.spotter.feature.settings.presentation.di.settingsPresentationModule
import com.example.spotter.feature.splash.presentation.di.splashPresentationModule
import org.koin.core.KoinApplication
import org.koin.core.context.startKoin
import org.koin.core.module.Module

private var koinInitialized = false

private val sharedModules = listOf(
    appModule,
    overpassApiModule,
    navigationModule,
    homeDataModule,
    homeDomainModule,
    homePresentationModule,
    mapDataModule,
    mapDomainModule,
    mapPresentationModule,
    favoritesDataModule,
    favoritesDomainModule,
    favoritesPresentationModule,
    settingsPresentationModule,
    splashPresentationModule,
)

fun startKoinWithModules(
    platformModules: List<Module> = emptyList(),
    appDeclaration: KoinApplication.() -> Unit = {},
) {
    if (koinInitialized) return

    startKoin {
        appDeclaration()
        modules(sharedModules + platformModules)
    }
    koinInitialized = true
}
