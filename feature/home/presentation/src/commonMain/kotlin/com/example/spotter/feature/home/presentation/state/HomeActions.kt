package com.example.spotter.feature.home.presentation.state

import com.example.spotter.core.spotui.SpotterTab
import com.example.spotter.core.spotui.component.SpotCardBounds
import com.example.spotter.core.spotui.SpotSearchSuggestion

internal interface HomeActions {
    fun onCategorySelected(category: String)
    fun onSearchTextChanged(query: String)
    fun onSearchFocusChanged(active: Boolean)
    fun onSearchClear()
    fun onSearchSuggestionSelected(suggestion: SpotSearchSuggestion)
    fun onSpotSelected(spotId: Long)
    fun onSpotCardClick(spotId: Long, sourceBounds: SpotCardBounds)
    fun onExpandedSpotDismiss()
    fun onExpandedSpotDismissAnimationEnd()
    fun onNavigateToSpot(spotId: Long)
    fun onFavoriteToggle(spotId: Long)
    fun onTabSelected(tab: SpotterTab)
    fun retry()
}
