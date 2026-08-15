package com.example.spotter.feature.favorites.presentation.navigate

import androidx.navigation3.runtime.EntryProviderScope
import androidx.navigation3.runtime.NavKey
import com.example.spotter.core.navigation.NavGraphProvider
import com.example.spotter.feature.favorites.contract.FavoritesScreenDestination
import com.example.spotter.feature.favorites.presentation.ui.FavoritesScreen

internal class FavoritesProvider : NavGraphProvider {
    override fun registerEntries(scope: EntryProviderScope<NavKey>) {
        scope.entry<FavoritesScreenDestination> {
            FavoritesScreen()
        }
    }
}
