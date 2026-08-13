package com.example.spotter.feature.splash.presentation.di

import com.example.spotter.feature.splash.presentation.ui.SplashViewModel
import org.koin.core.module.dsl.viewModelOf
import org.koin.dsl.module

val splashPresentationModule = module {
    viewModelOf(::SplashViewModel)
}
