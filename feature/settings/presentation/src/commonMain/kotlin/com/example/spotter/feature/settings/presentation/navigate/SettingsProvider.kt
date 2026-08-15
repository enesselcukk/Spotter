package com.example.spotter.feature.settings.presentation.navigate

import androidx.navigation3.runtime.EntryProviderScope
import androidx.navigation3.runtime.NavKey
import com.example.spotter.core.navigation.NavGraphProvider
import com.example.spotter.feature.settings.contract.SettingsScreenDestination
import com.example.spotter.feature.settings.presentation.ui.SettingsScreen

internal class SettingsProvider : NavGraphProvider {
    override fun registerEntries(scope: EntryProviderScope<NavKey>) {
        scope.entry<SettingsScreenDestination> {
            SettingsScreen()
        }
    }
}
