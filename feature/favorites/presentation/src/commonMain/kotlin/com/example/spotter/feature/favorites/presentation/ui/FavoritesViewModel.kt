package com.example.spotter.feature.favorites.presentation.ui

import androidx.lifecycle.viewModelScope
import com.example.spotter.core.navigation.NavigationManager
import com.example.spotter.core.navigation.switchTab
import com.example.spotter.core.presentation.CoreViewModel
import com.example.spotter.core.spotui.SpotterTab
import com.example.spotter.feature.favorites.contract.FavoritesScreenDestination
import com.example.spotter.feature.favorites.domain.usecase.ObserveFavoritesUseCase
import com.example.spotter.feature.favorites.domain.usecase.ToggleFavoriteUseCase
import com.example.spotter.feature.home.contract.HomeScreenDestination
import com.example.spotter.feature.settings.contract.SettingsScreenDestination
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class FavoritesViewModel(
    observeFavoritesUseCase: ObserveFavoritesUseCase,
    private val toggleFavoriteUseCase: ToggleFavoriteUseCase,
    private val navigationManager: NavigationManager,
) : CoreViewModel() {

    private val _uiState = MutableStateFlow(FavoritesUiState())
    val uiState: StateFlow<FavoritesUiState> = _uiState.asStateFlow()

    init {
        viewModelScope.launch {
            observeFavoritesUseCase().collect { favorites ->
                _uiState.update { it.copy(favorites = favorites) }
            }
        }
    }

    fun onFavoriteToggle(spotId: Long) {
        val spot = _uiState.value.favorites.find { it.id == spotId } ?: return
        viewModelScope.launch { toggleFavoriteUseCase(spot) }
    }

    fun onTabSelected(tab: SpotterTab) {
        if (tab == SpotterTab.Favorites) return

        val target = when (tab) {
            SpotterTab.Search -> HomeScreenDestination
            SpotterTab.Favorites -> FavoritesScreenDestination
            SpotterTab.Settings -> SettingsScreenDestination
        }
        navigationManager.switchTab(
            target = target,
            root = HomeScreenDestination,
            currentIsRoot = false,
        )
    }
}
