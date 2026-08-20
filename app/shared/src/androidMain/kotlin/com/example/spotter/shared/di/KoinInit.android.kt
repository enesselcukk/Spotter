package com.example.spotter.shared.di

import android.content.Context
import com.example.spotter.core.database.DatabaseFactory
import com.example.spotter.core.database.SpotterDatabase
import com.example.spotter.core.location.LocationPlatform
import com.example.spotter.feature.home.data.location.PlatformLocationProvider
import org.koin.android.ext.koin.androidContext
import org.koin.dsl.module

val androidPlatformModule = module {
    single<SpotterDatabase> {
        DatabaseFactory(get()).createDatabase()
    }
    single { PlatformLocationProvider() }
}

fun initAppKoin(context: Context) {
    LocationPlatform.initialize(context)
    startKoinWithModules(listOf(androidPlatformModule)) {
        androidContext(context)
    }
}
