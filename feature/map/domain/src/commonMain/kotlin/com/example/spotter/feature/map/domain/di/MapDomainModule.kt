package com.example.spotter.feature.map.domain.di

import com.example.spotter.feature.map.domain.usecase.GetRoutePlanUseCase
import org.koin.core.module.dsl.factoryOf
import org.koin.dsl.module

val mapDomainModule = module {
    factoryOf(::GetRoutePlanUseCase)
}
