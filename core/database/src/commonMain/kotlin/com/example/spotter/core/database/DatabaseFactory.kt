package com.example.spotter.core.database

import androidx.room3.RoomDatabase
import androidx.sqlite.driver.bundled.BundledSQLiteDriver
import kotlin.coroutines.CoroutineContext
import kotlin.coroutines.EmptyCoroutineContext

expect class DatabaseFactory {
    fun createDatabase(): SpotterDatabase
}

fun RoomDatabase.Builder<SpotterDatabase>.buildSpotterDatabase(
    queryCoroutineContext: CoroutineContext = EmptyCoroutineContext,
): SpotterDatabase =
    setDriver(BundledSQLiteDriver())
        .setQueryCoroutineContext(queryCoroutineContext)
        .build()
