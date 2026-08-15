package com.example.spotter.feature.home.contract

import androidx.navigation3.runtime.NavKey
import kotlinx.serialization.modules.SerializersModule
import kotlinx.serialization.modules.polymorphic
import kotlinx.serialization.modules.subclass

val homeNavSerializersModule = SerializersModule {
    polymorphic(NavKey::class) {
        subclass(HomeScreenDestination::class, HomeScreenDestination.serializer())
    }
}
