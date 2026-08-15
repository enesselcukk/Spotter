package com.example.spotter.shared.navigation

import androidx.savedstate.serialization.SavedStateConfiguration
import com.example.spotter.feature.favorites.contract.favoritesNavSerializersModule
import com.example.spotter.feature.home.contract.homeNavSerializersModule
import com.example.spotter.feature.map.contract.mapNavSerializersModule
import com.example.spotter.feature.settings.contract.settingsNavSerializersModule
import com.example.spotter.feature.splash.contract.splashNavSerializersModule
import kotlinx.serialization.modules.SerializersModule

val spotterNavSavedStateConfiguration = SavedStateConfiguration {
    serializersModule = SerializersModule {
        include(splashNavSerializersModule)
        include(homeNavSerializersModule)
        include(mapNavSerializersModule)
        include(favoritesNavSerializersModule)
        include(settingsNavSerializersModule)
    }
}
