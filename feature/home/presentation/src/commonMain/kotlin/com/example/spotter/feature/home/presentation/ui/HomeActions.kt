package com.example.spotter.feature.home.presentation.ui

internal interface HomeActions {
    fun onCategorySelected(category: String)
    fun onSpotSelected(spotId: Long)
    fun onViewModeToggle()
    fun onFavoriteToggle(spotId: Long)
    fun onBottomNavSelected(nav: HomeBottomNav)
    fun retry()
}
