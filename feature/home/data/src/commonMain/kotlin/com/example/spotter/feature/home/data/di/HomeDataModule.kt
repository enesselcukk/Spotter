package com.example.spotter.feature.home.data.di

import com.example.spotter.feature.home.data.location.LocationRepositoryImpl
import com.example.spotter.feature.home.data.network.HomeApi
import com.example.spotter.feature.home.data.repository.HomePreloadRepositoryImpl
import com.example.spotter.feature.home.data.repository.HomeRepositoryImpl
import com.example.spotter.feature.home.domain.repository.HomePreloadRepository
import com.example.spotter.feature.home.domain.repository.HomeRepository
import com.example.spotter.feature.home.domain.repository.LocationRepository
import org.koin.core.module.Module
import org.koin.dsl.module

val homeDataModule: Module = module {
    single { HomeApi(httpClient = get()) }
    single<HomeRepository> { HomeRepositoryImpl(homeApi = get()) }
    single<LocationRepository> { LocationRepositoryImpl(platformLocationProvider = get()) }
    single<HomePreloadRepository> { HomePreloadRepositoryImpl() }
}
