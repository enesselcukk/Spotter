package com.example.spotter.feature.splash.presentation.navigate

import androidx.navigation3.runtime.EntryProviderScope
import androidx.navigation3.runtime.NavKey
import com.example.spotter.core.navigation.NavGraphProvider
import com.example.spotter.feature.splash.contract.SplashScreenDestination
import com.example.spotter.feature.splash.presentation.ui.SplashScreen

internal class SplashProvider : NavGraphProvider {
    override fun registerEntries(scope: EntryProviderScope<NavKey>) {
        scope.entry<SplashScreenDestination> {
            SplashScreen()
        }
    }
}
