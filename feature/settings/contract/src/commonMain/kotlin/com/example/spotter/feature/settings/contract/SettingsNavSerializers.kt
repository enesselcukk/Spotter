package com.example.spotter.feature.settings.contract

import androidx.navigation3.runtime.NavKey
import kotlinx.serialization.modules.SerializersModule
import kotlinx.serialization.modules.polymorphic
import kotlinx.serialization.modules.subclass

val settingsNavSerializersModule = SerializersModule {
    polymorphic(NavKey::class) {
        subclass(SettingsScreenDestination::class, SettingsScreenDestination.serializer())
    }
}
