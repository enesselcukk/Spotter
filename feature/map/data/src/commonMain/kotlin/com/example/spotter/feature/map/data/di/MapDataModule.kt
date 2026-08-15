package com.example.spotter.feature.map.data.di

import com.example.spotter.feature.map.data.network.RoutingApi
import com.example.spotter.feature.map.data.repository.MapSpotsHandoffImpl
import com.example.spotter.feature.map.data.repository.RouteRepositoryImpl
import com.example.spotter.feature.map.domain.repository.MapSpotsHandoff
import com.example.spotter.feature.map.domain.repository.RouteRepository
import org.koin.core.module.Module
import org.koin.dsl.module

val mapDataModule: Module = module {
    single { RoutingApi(httpClient = get()) }
    single<RouteRepository> { RouteRepositoryImpl(routingApi = get()) }
    single<MapSpotsHandoff> { MapSpotsHandoffImpl() }
    bindNavigationLocationTracker()
}

internal expect fun Module.bindNavigationLocationTracker()
