package com.example.spotter.feature.splash.contract

import androidx.navigation3.runtime.NavKey
import kotlinx.serialization.modules.SerializersModule
import kotlinx.serialization.modules.polymorphic
import kotlinx.serialization.modules.subclass

val splashNavSerializersModule = SerializersModule {
    polymorphic(NavKey::class) {
        subclass(SplashScreenDestination::class, SplashScreenDestination.serializer())
    }
}
