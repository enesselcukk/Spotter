package com.example.spotter.feature.home.presentation.di

import com.example.spotter.core.navigation.NavGraphProvider
import com.example.spotter.feature.home.presentation.navigate.HomeProvider
import com.example.spotter.feature.home.presentation.ui.HomeViewModel
import com.example.spotter.feature.home.presentation.ui.SettingsViewModel
import org.koin.core.module.dsl.viewModelOf
import org.koin.core.qualifier.named
import org.koin.dsl.module

val homePresentationModule = module {
    single<NavGraphProvider>(named("HomeProvider")) {
        HomeProvider()
    }

    viewModelOf(::HomeViewModel)
    viewModelOf(::SettingsViewModel)
}
