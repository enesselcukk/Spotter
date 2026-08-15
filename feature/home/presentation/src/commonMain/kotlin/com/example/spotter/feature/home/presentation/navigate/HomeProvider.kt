package com.example.spotter.feature.home.presentation.navigate

import androidx.navigation3.runtime.EntryProviderScope
import androidx.navigation3.runtime.NavKey
import com.example.spotter.core.navigation.NavGraphProvider
import com.example.spotter.feature.home.contract.HomeScreenDestination
import com.example.spotter.feature.home.presentation.ui.HomeScreen

internal class HomeProvider : NavGraphProvider {
    override fun registerEntries(scope: EntryProviderScope<NavKey>) {
        scope.entry<HomeScreenDestination> {
            HomeScreen()
        }
    }
}
