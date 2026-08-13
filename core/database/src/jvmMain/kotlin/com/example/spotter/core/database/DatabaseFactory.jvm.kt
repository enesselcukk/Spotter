package com.example.spotter.core.database

import androidx.room3.Room
import kotlinx.coroutines.Dispatchers
import java.io.File

actual class DatabaseFactory {
    actual fun createDatabase(): SpotterDatabase {
        val dbFile = File(System.getProperty("java.io.tmpdir"), "spotter.db")
        return Room.databaseBuilder<SpotterDatabase>(
            name = dbFile.absolutePath,
        ).buildSpotterDatabase(Dispatchers.IO)
    }
}
