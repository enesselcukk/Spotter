package com.example.spotter.feature.home.presentation.ui

import com.example.spotter.core.spotui.SpotterTab
import com.example.spotter.core.spotui.SpotSearchSuggestion

internal interface HomeActions {
    fun onCategorySelected(category: String)
    fun onSearchTextChanged(query: String)
    fun onSearchFocusChanged(active: Boolean)
    fun onSearchClear()
    fun onSearchSuggestionSelected(suggestion: SpotSearchSuggestion)
    fun onSpotSelected(spotId: Long)
    fun onOpenMap()
    fun onFavoriteToggle(spotId: Long)
    fun onTabSelected(tab: SpotterTab)
    fun retry()
}
