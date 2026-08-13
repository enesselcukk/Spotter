package com.example.spotter.core.database

import android.content.Context
import androidx.room3.Room
import kotlinx.coroutines.Dispatchers

actual class DatabaseFactory(
    private val context: Context,
) {
    actual fun createDatabase(): SpotterDatabase =
        Room.databaseBuilder<SpotterDatabase>(
            context = context.applicationContext,
            name = context.applicationContext.getDatabasePath("spotter.db").absolutePath,
        ).buildSpotterDatabase(Dispatchers.IO)
}
