package com.example.spotter.feature.home.presentation.ui

import com.example.spotter.core.datastore.DefaultHomeViewMode
import com.example.spotter.core.datastore.DistanceUnit
import com.example.spotter.core.datastore.ListSortOrder
import com.example.spotter.core.datastore.ThemeMode

enum class SettingsPicker {
    Language,
    Theme,
    DistanceUnit,
    ListPreference,
}

data class SettingsUiState(
    val themeMode: ThemeMode = ThemeMode.SYSTEM,
    val language: String = "en",
    val distanceUnit: DistanceUnit = DistanceUnit.KILOMETERS,
    val autoApplyLocalization: Boolean = true,
    val defaultHomeViewMode: DefaultHomeViewMode = DefaultHomeViewMode.LIST,
    val listSortOrder: ListSortOrder = ListSortOrder.DISTANCE,
    val rememberLastCategory: Boolean = true,
    val activePicker: SettingsPicker? = null,
)

fun DefaultHomeViewMode.toHomeViewMode(): HomeViewMode = when (this) {
    DefaultHomeViewMode.LIST -> HomeViewMode.List
    DefaultHomeViewMode.MAP -> HomeViewMode.Map
}

fun HomeViewMode.toDefaultHomeViewMode(): DefaultHomeViewMode = when (this) {
    HomeViewMode.List -> DefaultHomeViewMode.LIST
    HomeViewMode.Map -> DefaultHomeViewMode.MAP
}
