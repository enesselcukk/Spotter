package com.example.spotter.feature.settings.presentation.di

import com.example.spotter.core.navigation.NavGraphProvider
import com.example.spotter.feature.settings.presentation.navigate.SettingsProvider
import com.example.spotter.feature.settings.presentation.ui.SettingsViewModel
import org.koin.core.module.dsl.viewModelOf
import org.koin.core.qualifier.named
import org.koin.dsl.module

val settingsPresentationModule = module {
    single<NavGraphProvider>(named("SettingsProvider")) {
        SettingsProvider()
    }

    viewModelOf(::SettingsViewModel)
}
