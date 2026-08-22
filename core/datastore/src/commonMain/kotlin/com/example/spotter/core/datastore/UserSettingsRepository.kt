package com.example.spotter.core.datastore

import kotlinx.coroutines.flow.Flow

interface UserSettingsRepository {
    val themeMode: Flow<ThemeMode>
    val language: Flow<String>
    val apiKey: Flow<String>
    val distanceUnit: Flow<DistanceUnit>
    val autoApplyLocalization: Flow<Boolean>
    val defaultHomeViewMode: Flow<DefaultHomeViewMode>
    val listSortOrder: Flow<ListSortOrder>
    val rememberLastCategory: Flow<Boolean>
    val lastSelectedCategory: Flow<String>

    suspend fun setThemeMode(mode: ThemeMode)
    suspend fun setLanguage(language: String)
    suspend fun setApiKey(apiKey: String)
    suspend fun setDistanceUnit(unit: DistanceUnit)
    suspend fun setAutoApplyLocalization(enabled: Boolean)
    suspend fun setDefaultHomeViewMode(mode: DefaultHomeViewMode)
    suspend fun setListSortOrder(order: ListSortOrder)
    suspend fun setRememberLastCategory(enabled: Boolean)
    suspend fun setLastSelectedCategory(category: String)
}
