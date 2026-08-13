package com.example.spotter.core.database

import androidx.room3.Room
import kotlinx.coroutines.Dispatchers
import platform.Foundation.NSTemporaryDirectory

actual class DatabaseFactory {
    actual fun createDatabase(): SpotterDatabase {
        val dbPath = NSTemporaryDirectory() + "spotter.db"
        return Room.databaseBuilder<SpotterDatabase>(
            name = dbPath,
        ).buildSpotterDatabase(Dispatchers.Default)
    }
}
