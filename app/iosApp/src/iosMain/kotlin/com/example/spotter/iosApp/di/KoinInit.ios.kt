package com.example.spotter.iosApp.di

import com.example.spotter.core.database.DatabaseFactory
import com.example.spotter.core.database.SpotterDatabase
import com.example.spotter.feature.home.data.location.PlatformLocationProvider
import com.example.spotter.shared.di.startKoinWithModules
import org.koin.dsl.module

private val iosPlatformModule = module {
    single<SpotterDatabase> {
        DatabaseFactory().createDatabase()
    }
    single { PlatformLocationProvider() }
}

fun initAppKoin() {
    startKoinWithModules(listOf(iosPlatformModule))
}
