package com.example.spotter.feature.splash.presentation.di

import com.example.spotter.core.navigation.NavGraphProvider
import com.example.spotter.feature.splash.presentation.navigate.SplashProvider
import com.example.spotter.feature.splash.presentation.ui.SplashViewModel
import org.koin.core.module.dsl.viewModelOf
import org.koin.core.qualifier.named
import org.koin.dsl.module

val splashPresentationModule = module {
    single<NavGraphProvider>(named("SplashProvider")) {
        SplashProvider()
    }

    viewModelOf(::SplashViewModel)
}
