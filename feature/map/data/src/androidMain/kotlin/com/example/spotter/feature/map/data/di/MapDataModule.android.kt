package com.example.spotter.feature.map.data.di

import com.example.spotter.feature.map.data.location.NavigationLocationTrackerImpl
import com.example.spotter.feature.map.domain.repository.NavigationLocationTracker
import org.koin.core.module.Module

internal actual fun Module.bindNavigationLocationTracker() {
    single<NavigationLocationTracker> {
        NavigationLocationTrackerImpl(context = get())
    }
}
