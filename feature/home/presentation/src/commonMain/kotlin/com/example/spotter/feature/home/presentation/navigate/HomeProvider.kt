package com.example.spotter.feature.home.presentation.navigate

import androidx.navigation.NavGraphBuilder
import androidx.navigation.compose.composable
import com.example.spotter.core.navigation.NavGraphProvider
import com.example.spotter.feature.home.contract.HomeScreenDestination
import com.example.spotter.feature.home.presentation.ui.HomeScreen

internal class HomeProvider : NavGraphProvider {
    override fun registerGraph(provider: NavGraphBuilder) {
        provider.apply {
            composable<HomeScreenDestination> {
                HomeScreen()
            }
        }
    }
}
