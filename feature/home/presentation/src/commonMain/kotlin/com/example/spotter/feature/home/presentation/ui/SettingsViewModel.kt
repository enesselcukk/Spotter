package com.example.spotter.feature.home.presentation.ui

import androidx.lifecycle.viewModelScope
import com.example.spotter.core.datastore.DefaultHomeViewMode
import com.example.spotter.core.datastore.DistanceUnit
import com.example.spotter.core.datastore.ListSortOrder
import com.example.spotter.core.datastore.ThemeMode
import com.example.spotter.core.datastore.UserSettingsRepository
import com.example.spotter.core.presentation.CoreViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class SettingsViewModel(
    private val userSettingsRepository: UserSettingsRepository,
) : CoreViewModel() {

    private val _uiState = MutableStateFlow(SettingsUiState())
    val uiState: StateFlow<SettingsUiState> = _uiState.asStateFlow()

    init {
        viewModelScope.launch {
            combine(
                combine(
                    userSettingsRepository.themeMode,
                    userSettingsRepository.language,
                    userSettingsRepository.distanceUnit,
                    userSettingsRepository.autoApplyLocalization,
                ) { themeMode, language, distanceUnit, autoApplyLocalization ->
                    SettingsUiState(
                        themeMode = themeMode,
                        language = language,
                        distanceUnit = distanceUnit,
                        autoApplyLocalization = autoApplyLocalization,
                    )
                },
                combine(
                    userSettingsRepository.defaultHomeViewMode,
                    userSettingsRepository.listSortOrder,
                    userSettingsRepository.rememberLastCategory,
                ) { defaultHomeViewMode, listSortOrder, rememberLastCategory ->
                    Triple(defaultHomeViewMode, listSortOrder, rememberLastCategory)
                },
            ) { base, listPrefs ->
                base.copy(
                    defaultHomeViewMode = listPrefs.first,
                    listSortOrder = listPrefs.second,
                    rememberLastCategory = listPrefs.third,
                )
            }.collect { state ->
                _uiState.update { current ->
                    state.copy(activePicker = current.activePicker)
                }
            }
        }
    }

    fun openPicker(picker: SettingsPicker) {
        _uiState.update { it.copy(activePicker = picker) }
    }

    fun dismissPicker() {
        _uiState.update { it.copy(activePicker = null) }
    }

    fun selectLanguage(language: String) {
        viewModelScope.launch {
            userSettingsRepository.setLanguage(language)
            userSettingsRepository.setAutoApplyLocalization(false)
            dismissPicker()
        }
    }

    fun selectThemeMode(mode: ThemeMode) {
        viewModelScope.launch {
            userSettingsRepository.setThemeMode(mode)
            dismissPicker()
        }
    }

    fun selectDistanceUnit(unit: DistanceUnit) {
        viewModelScope.launch {
            userSettingsRepository.setDistanceUnit(unit)
            dismissPicker()
        }
    }

    fun selectDefaultHomeViewMode(mode: DefaultHomeViewMode) {
        viewModelScope.launch {
            userSettingsRepository.setDefaultHomeViewMode(mode)
        }
    }

    fun selectListSortOrder(order: ListSortOrder) {
        viewModelScope.launch {
            userSettingsRepository.setListSortOrder(order)
        }
    }

    fun setRememberLastCategory(enabled: Boolean) {
        viewModelScope.launch {
            userSettingsRepository.setRememberLastCategory(enabled)
        }
    }

    fun setAutoApplyLocalization(enabled: Boolean) {
        viewModelScope.launch {
            userSettingsRepository.setAutoApplyLocalization(enabled)
        }
    }
}
