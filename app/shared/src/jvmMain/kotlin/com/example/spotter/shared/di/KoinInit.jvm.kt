package com.example.spotter.shared.di

import com.example.spotter.core.database.DatabaseFactory
import com.example.spotter.core.database.SpotterDatabase
import com.example.spotter.feature.home.data.location.PlatformLocationProvider
import org.koin.dsl.module

val jvmPlatformModule = module {
    single<SpotterDatabase> {
        DatabaseFactory().createDatabase()
    }
    single { PlatformLocationProvider() }
}

fun initAppKoin() {
    startKoinWithModules(listOf(jvmPlatformModule))
}
