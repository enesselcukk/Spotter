package com.example.spotter.feature.map.presentation.navigate

import androidx.navigation3.runtime.EntryProviderScope
import androidx.navigation3.runtime.NavKey
import com.example.spotter.core.navigation.NavGraphProvider
import com.example.spotter.feature.map.contract.MapScreenDestination
import com.example.spotter.feature.map.presentation.ui.MapScreen

internal class MapProvider : NavGraphProvider {
    override fun registerEntries(scope: EntryProviderScope<NavKey>) {
        scope.entry<MapScreenDestination> { destination ->
            MapScreen(destination = destination)
        }
    }
}
