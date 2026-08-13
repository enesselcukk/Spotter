package com.example.spotter.core.datastore

import com.russhwolf.settings.MapSettings
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.test.runTest

class UserSettingsRepositoryTest {
    @Test
    fun settings_roundTrip() = runTest {
        val repository = DefaultUserSettingsRepository(MapSettings())

        repository.setThemeMode(ThemeMode.DARK)
        repository.setLanguage("tr")
        repository.setApiKey("secret-key")
        repository.setDistanceUnit(DistanceUnit.MILES)
        repository.setAutoApplyLocalization(false)
        repository.setDefaultHomeViewMode(DefaultHomeViewMode.MAP)
        repository.setListSortOrder(ListSortOrder.NAME_ASC)
        repository.setRememberLastCategory(true)
        repository.setLastSelectedCategory("fuel")

        assertEquals(ThemeMode.DARK, repository.themeMode.first())
        assertEquals("tr", repository.language.first())
        assertEquals("secret-key", repository.apiKey.first())
        assertEquals(DistanceUnit.MILES, repository.distanceUnit.first())
        assertEquals(false, repository.autoApplyLocalization.first())
        assertEquals(DefaultHomeViewMode.MAP, repository.defaultHomeViewMode.first())
        assertEquals(ListSortOrder.NAME_ASC, repository.listSortOrder.first())
        assertEquals(true, repository.rememberLastCategory.first())
        assertEquals("fuel", repository.lastSelectedCategory.first())
    }
}
