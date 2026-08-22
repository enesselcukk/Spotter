package com.example.spotter.core.datastore

import com.russhwolf.settings.Settings
import com.russhwolf.settings.set
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow

class DefaultUserSettingsRepository(
    private val settings: Settings = Settings(),
) : UserSettingsRepository {
    private val themeModeState = MutableStateFlow(readThemeMode())
    private val languageState = MutableStateFlow(readLanguage())
    private val apiKeyState = MutableStateFlow(readApiKey())
    private val distanceUnitState = MutableStateFlow(readDistanceUnit())
    private val autoApplyLocalizationState = MutableStateFlow(readAutoApplyLocalization())
    private val defaultHomeViewModeState = MutableStateFlow(readDefaultHomeViewMode())
    private val listSortOrderState = MutableStateFlow(readListSortOrder())
    private val rememberLastCategoryState = MutableStateFlow(readRememberLastCategory())
    private val lastSelectedCategoryState = MutableStateFlow(readLastSelectedCategory())

    override val themeMode: Flow<ThemeMode> = themeModeState.asStateFlow()
    override val language: Flow<String> = languageState.asStateFlow()
    override val apiKey: Flow<String> = apiKeyState.asStateFlow()
    override val distanceUnit: Flow<DistanceUnit> = distanceUnitState.asStateFlow()
    override val autoApplyLocalization: Flow<Boolean> = autoApplyLocalizationState.asStateFlow()
    override val defaultHomeViewMode: Flow<DefaultHomeViewMode> = defaultHomeViewModeState.asStateFlow()
    override val listSortOrder: Flow<ListSortOrder> = listSortOrderState.asStateFlow()
    override val rememberLastCategory: Flow<Boolean> = rememberLastCategoryState.asStateFlow()
    override val lastSelectedCategory: Flow<String> = lastSelectedCategoryState.asStateFlow()

    override suspend fun setThemeMode(mode: ThemeMode) {
        settings[SettingsKeys.THEME_MODE] = mode.name
        themeModeState.value = mode
    }

    override suspend fun setLanguage(language: String) {
        val normalized = AppLanguage.fromTag(language).tag
        settings[SettingsKeys.LANGUAGE] = normalized
        languageState.value = normalized
    }

    override suspend fun setApiKey(apiKey: String) {
        settings[SettingsKeys.API_KEY] = apiKey
        apiKeyState.value = apiKey
    }

    override suspend fun setDistanceUnit(unit: DistanceUnit) {
        settings[SettingsKeys.DISTANCE_UNIT] = unit.name
        distanceUnitState.value = unit
    }

    override suspend fun setAutoApplyLocalization(enabled: Boolean) {
        settings[SettingsKeys.AUTO_APPLY_LOCALIZATION] = enabled
        autoApplyLocalizationState.value = enabled
    }

    override suspend fun setDefaultHomeViewMode(mode: DefaultHomeViewMode) {
        settings[SettingsKeys.DEFAULT_HOME_VIEW_MODE] = mode.name
        defaultHomeViewModeState.value = mode
    }

    override suspend fun setListSortOrder(order: ListSortOrder) {
        settings[SettingsKeys.LIST_SORT_ORDER] = order.name
        listSortOrderState.value = order
    }

    override suspend fun setRememberLastCategory(enabled: Boolean) {
        settings[SettingsKeys.REMEMBER_LAST_CATEGORY] = enabled
        rememberLastCategoryState.value = enabled
    }

    override suspend fun setLastSelectedCategory(category: String) {
        settings[SettingsKeys.LAST_SELECTED_CATEGORY] = category
        lastSelectedCategoryState.value = category
    }

    private fun readThemeMode(): ThemeMode =
        themeModeFromStorage(settings.getStringOrNull(SettingsKeys.THEME_MODE))

    private fun readLanguage(): String =
        AppLanguage.fromTag(settings.getStringOrNull(SettingsKeys.LANGUAGE) ?: AppLanguage.ENGLISH.tag).tag

    private fun readApiKey(): String =
        settings.getStringOrNull(SettingsKeys.API_KEY) ?: ""

    private fun readDistanceUnit(): DistanceUnit =
        distanceUnitFromStorage(settings.getStringOrNull(SettingsKeys.DISTANCE_UNIT))

    private fun readAutoApplyLocalization(): Boolean =
        settings.getBooleanOrNull(SettingsKeys.AUTO_APPLY_LOCALIZATION) ?: true

    private fun readDefaultHomeViewMode(): DefaultHomeViewMode =
        defaultHomeViewModeFromStorage(settings.getStringOrNull(SettingsKeys.DEFAULT_HOME_VIEW_MODE))

    private fun readListSortOrder(): ListSortOrder =
        listSortOrderFromStorage(settings.getStringOrNull(SettingsKeys.LIST_SORT_ORDER))

    private fun readRememberLastCategory(): Boolean =
        settings.getBooleanOrNull(SettingsKeys.REMEMBER_LAST_CATEGORY) ?: true

    private fun readLastSelectedCategory(): String =
        settings.getStringOrNull(SettingsKeys.LAST_SELECTED_CATEGORY) ?: "charging_station"
}
