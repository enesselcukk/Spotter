package com.example.spotter.feature.home.presentation.navigate

import com.example.spotter.core.navigation.NavigationCommand
import com.example.spotter.core.navigation.NavigationManager
import com.example.spotter.core.navigation.switchTab
import com.example.spotter.core.spotui.SpotterTab
import com.example.spotter.feature.favorites.contract.FavoritesScreenDestination
import com.example.spotter.feature.home.contract.HomeScreenDestination
import com.example.spotter.feature.home.presentation.state.HomeUiState
import com.example.spotter.feature.map.contract.MapScreenDestination
import com.example.spotter.feature.map.domain.repository.MapSpotsHandoff
import com.example.spotter.feature.settings.contract.SettingsScreenDestination

internal class HomeNavigator(
    private val navigationManager: NavigationManager,
    private val mapSpotsHandoff: MapSpotsHandoff,
) {
    fun openMap(state: HomeUiState.Success, focusedSpotId: Long? = state.selectedSpotId) {
        mapSpotsHandoff.publish(state.spots)
        navigationManager.navigate(
            NavigationCommand.NavigateTo(
                to = MapScreenDestination(
                    userLatitude = state.userLatitude,
                    userLongitude = state.userLongitude,
                    focusedSpotId = focusedSpotId,
                    category = state.selectedCategory,
                ),
            ),
        )
    }

    fun switchRootTab(tab: SpotterTab) {
        val target = when (tab) {
            SpotterTab.Favorites -> FavoritesScreenDestination
            SpotterTab.Settings -> SettingsScreenDestination
            SpotterTab.Search, SpotterTab.Map -> return
        }
        navigationManager.switchTab(
            target = target,
            root = HomeScreenDestination,
            currentIsRoot = true,
        )
    }
}
