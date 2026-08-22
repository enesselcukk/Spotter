package com.example.spotter.feature.home.presentation.state

import com.example.spotter.core.datastore.DefaultHomeViewMode
import com.example.spotter.core.datastore.ListSortOrder
import com.example.spotter.core.spotui.SpotCategories

internal data class HomeListPreferences(
    val sortOrder: ListSortOrder = ListSortOrder.DISTANCE,
    val defaultViewMode: DefaultHomeViewMode = DefaultHomeViewMode.LIST,
    val rememberLastCategory: Boolean = true,
    val lastSelectedCategory: String = SpotCategories.CHARGING,
) {
    val preferredCategory: String
        get() = lastSelectedCategory
            .takeIf { rememberLastCategory && it in HomeUiState.defaultCategories }
            ?: SpotCategories.CHARGING
}
