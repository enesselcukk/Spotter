package com.example.spotter.feature.home.domain.util

import com.example.spotter.core.datastore.ListSortOrder
import com.example.spotter.feature.home.domain.model.SpotDto

object SpotListSorter {
    fun sort(
        spots: List<SpotDto>,
        order: ListSortOrder,
    ): List<SpotDto> = when (order) {
        ListSortOrder.DISTANCE -> spots.sortedWith(
            compareBy<SpotDto> { it.distanceMeters ?: Double.MAX_VALUE }
                .thenBy { it.displayName.lowercase() },
        )

        ListSortOrder.NAME_ASC -> spots.sortedBy { it.displayName.lowercase() }

        ListSortOrder.NAME_DESC -> spots.sortedByDescending { it.displayName.lowercase() }
    }
}

private val SpotDto.displayName: String
    get() = name?.takeIf { it.isNotBlank() } ?: "zzzz"
