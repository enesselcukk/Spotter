package com.example.spotter.shared.di

import com.example.spotter.core.datastore.DefaultUserSettingsRepository
import com.example.spotter.core.datastore.UserSettingsRepository
import org.koin.dsl.module

val appModule = module {
    single<UserSettingsRepository> { DefaultUserSettingsRepository() }
}
