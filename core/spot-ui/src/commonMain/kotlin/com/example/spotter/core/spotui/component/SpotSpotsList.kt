package com.example.spotter.core.spotui.component

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.itemsIndexed
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.example.spotter.core.datastore.DefaultHomeViewMode
import com.example.spotter.feature.home.domain.model.SpotDto

@Composable
fun SpotSpotsList(
    spots: List<SpotDto>,
    layoutMode: DefaultHomeViewMode,
    favoriteIds: Set<Long>,
    selectedSpotId: Long?,
    onNavigate: (Long) -> Unit,
    onFavoriteToggle: (Long) -> Unit,
    onSpotClick: (Long, SpotCardBounds) -> Unit,
    expandedSpotId: Long? = null,
    modifier: Modifier = Modifier,
    alwaysHighlighted: Boolean = false,
) {
    when (layoutMode) {
        DefaultHomeViewMode.LIST -> {
            LazyColumn(
                modifier = modifier.fillMaxWidth(),
                contentPadding = PaddingValues(start = 16.dp, end = 16.dp, top = 14.dp, bottom = 108.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp),
            ) {
                itemsIndexed(spots, key = { _, spot -> spot.id }) { index, spot ->
                    SpotDetailCard(
                        spot = spot,
                        isFavorite = spot.id in favoriteIds,
                        markerIndex = index + 1,
                        onNavigate = onNavigate,
                        onFavoriteToggle = onFavoriteToggle,
                        onCardClick = onSpotClick,
                        isSourceHidden = spot.id == expandedSpotId,
                        highlighted = alwaysHighlighted || spot.id == selectedSpotId,
                    )
                }
            }
        }

        DefaultHomeViewMode.COLUMN -> {
            LazyVerticalGrid(
                columns = GridCells.Fixed(2),
                modifier = modifier.fillMaxWidth(),
                contentPadding = PaddingValues(start = 16.dp, end = 16.dp, top = 14.dp, bottom = 108.dp),
                horizontalArrangement = Arrangement.spacedBy(10.dp),
                verticalArrangement = Arrangement.spacedBy(10.dp),
            ) {
                itemsIndexed(spots, key = { _, spot -> spot.id }) { index, spot ->
                    SpotColumnCard(
                        spot = spot,
                        isFavorite = spot.id in favoriteIds,
                        markerIndex = index + 1,
                        onNavigate = onNavigate,
                        onFavoriteToggle = onFavoriteToggle,
                        onCardClick = onSpotClick,
                        isSourceHidden = spot.id == expandedSpotId,
                        highlighted = alwaysHighlighted || spot.id == selectedSpotId,
                    )
                }
            }
        }
    }
}
