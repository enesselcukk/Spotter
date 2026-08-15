package com.example.spotter.feature.favorites.contract

import androidx.navigation3.runtime.NavKey
import kotlinx.serialization.modules.SerializersModule
import kotlinx.serialization.modules.polymorphic
import kotlinx.serialization.modules.subclass

val favoritesNavSerializersModule = SerializersModule {
    polymorphic(NavKey::class) {
        subclass(FavoritesScreenDestination::class, FavoritesScreenDestination.serializer())
    }
}
