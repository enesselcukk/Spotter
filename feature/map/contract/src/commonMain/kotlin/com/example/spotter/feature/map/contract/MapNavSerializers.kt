package com.example.spotter.feature.map.contract

import androidx.navigation3.runtime.NavKey
import kotlinx.serialization.modules.SerializersModule
import kotlinx.serialization.modules.polymorphic
import kotlinx.serialization.modules.subclass

val mapNavSerializersModule = SerializersModule {
    polymorphic(NavKey::class) {
        subclass(MapScreenDestination::class, MapScreenDestination.serializer())
    }
}
