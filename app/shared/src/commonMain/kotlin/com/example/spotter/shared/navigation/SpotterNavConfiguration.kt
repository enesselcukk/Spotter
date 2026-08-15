package com.example.spotter.shared.navigation

import androidx.savedstate.serialization.SavedStateConfiguration
import com.example.spotter.feature.home.contract.homeNavSerializersModule
import com.example.spotter.feature.splash.contract.splashNavSerializersModule
import kotlinx.serialization.modules.SerializersModule

val spotterNavSavedStateConfiguration = SavedStateConfiguration {
    serializersModule = SerializersModule {
        include(splashNavSerializersModule)
        include(homeNavSerializersModule)
    }
}
