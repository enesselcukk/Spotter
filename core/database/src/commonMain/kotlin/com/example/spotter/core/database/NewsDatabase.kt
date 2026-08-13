package com.example.spotter.core.database

import androidx.room3.ConstructedBy
import androidx.room3.Database
import androidx.room3.RoomDatabase
import androidx.room3.RoomDatabaseConstructor
import com.example.spotter.core.database.dao.ArticleDao
import com.example.spotter.core.database.model.ArticleEntity

@Database(
    entities = [ArticleEntity::class],
    version = 1,
)
@ConstructedBy(SpotterDatabaseConstructor::class)
abstract class SpotterDatabase : RoomDatabase() {
    abstract fun articleDao(): ArticleDao
}

@Suppress("KotlinNoActualForExpect")
expect object SpotterDatabaseConstructor : RoomDatabaseConstructor<SpotterDatabase> {
    override fun initialize(): SpotterDatabase
}
