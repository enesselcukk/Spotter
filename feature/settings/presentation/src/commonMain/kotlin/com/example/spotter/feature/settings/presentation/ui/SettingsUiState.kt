package com.example.spotter.feature.settings.presentation.ui

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
