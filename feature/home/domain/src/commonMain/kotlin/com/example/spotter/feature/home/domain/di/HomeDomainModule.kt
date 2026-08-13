package com.example.spotter.feature.home.domain.di

import com.example.spotter.feature.home.domain.usecase.GetNearbySpotsUseCase
import com.example.spotter.feature.home.domain.usecase.PreloadHomeDataUseCase
import com.example.spotter.feature.home.domain.usecase.ResolveSearchLocationUseCase
import org.koin.dsl.module

val homeDomainModule = module {
    factory { GetNearbySpotsUseCase(homeRepository = get()) }
    factory { ResolveSearchLocationUseCase(locationRepository = get()) }
    factory { PreloadHomeDataUseCase(
        resolveSearchLocationUseCase = get(),
        homeRepository = get(),
        homePreloadRepository = get(),
    ) }
}
