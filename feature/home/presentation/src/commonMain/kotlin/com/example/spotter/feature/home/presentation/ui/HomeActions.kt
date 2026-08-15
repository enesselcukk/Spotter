package com.example.spotter.feature.home.presentation.ui

import com.example.spotter.core.spotui.SpotterTab

internal interface HomeActions {
    fun onCategorySelected(category: String)
    fun onSpotSelected(spotId: Long)
    fun onOpenMap()
    fun onFavoriteToggle(spotId: Long)
    fun onTabSelected(tab: SpotterTab)
    fun retry()
}
