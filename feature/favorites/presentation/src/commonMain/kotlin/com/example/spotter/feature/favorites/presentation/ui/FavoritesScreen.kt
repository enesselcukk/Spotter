package com.example.spotter.feature.favorites.presentation.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.luminance
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.spotter.core.designsystem.component.spotterStatusBarsPadding
import com.example.spotter.core.spotui.SpotterTab
import com.example.spotter.core.spotui.component.CarbonFiberBackground
import com.example.spotter.core.spotui.component.SpotCategoryChips
import com.example.spotter.core.spotui.component.SpotDetailCard
import com.example.spotter.core.spotui.component.SpotSearchBar
import com.example.spotter.core.spotui.component.SpotSearchSuggestionsPanel
import com.example.spotter.core.spotui.component.SpotterBottomBar
import com.example.spotter.core.spotui.platform.rememberDirectionsLauncher
import com.example.spotter.feature.favorites.presentation.generated.resources.Res
import com.example.spotter.feature.favorites.presentation.generated.resources.favorites_empty
import com.example.spotter.feature.favorites.presentation.generated.resources.favorites_empty_category
import com.example.spotter.feature.favorites.presentation.generated.resources.favorites_empty_hint
import com.example.spotter.feature.favorites.presentation.generated.resources.favorites_search_placeholder
import com.example.spotter.feature.home.domain.model.SpotDto
import org.jetbrains.compose.resources.stringResource
import org.koin.compose.viewmodel.koinViewModel

@Composable
fun FavoritesScreen(
    viewModel: FavoritesViewModel = koinViewModel(),
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    val launchDirections = rememberDirectionsLauncher()
    val colors = MaterialTheme.colorScheme
    val isDark = colors.background.luminance() < 0.5f

    CarbonFiberBackground(modifier = Modifier.fillMaxSize()) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .then(if (isDark) Modifier else Modifier.background(colors.background))
                .spotterStatusBarsPadding(),
        ) {
            SpotSearchBar(
                query = uiState.searchText,
                onQueryChange = viewModel::onSearchTextChanged,
                placeholder = stringResource(Res.string.favorites_search_placeholder),
                onFocusChanged = viewModel::onSearchFocusChanged,
                onClear = viewModel::onSearchClear,
            )

            if (uiState.isSearchActive) {
                SpotSearchSuggestionsPanel(
                    suggestions = uiState.searchSuggestions,
                    onSuggestionSelected = viewModel::onSearchSuggestionSelected,
                )
                Box(modifier = Modifier.weight(1f))
            } else {
                Spacer(modifier = Modifier.height(12.dp))
                SpotCategoryChips(
                    categories = uiState.categories,
                    selectedCategory = uiState.selectedCategory,
                    onCategorySelected = viewModel::onCategorySelected,
                )

                FavoritesList(
                    favorites = uiState.filteredFavorites,
                    totalCount = uiState.favorites.size,
                    onDirections = launchDirections,
                    onFavoriteToggle = viewModel::onFavoriteToggle,
                    modifier = Modifier.weight(1f),
                )
            }

            SpotterBottomBar(
                selected = SpotterTab.Favorites,
                onSelected = viewModel::onTabSelected,
            )
        }
    }
}

@Composable
private fun FavoritesList(
    favorites: List<SpotDto>,
    totalCount: Int,
    onDirections: (Double, Double, String?) -> Unit,
    onFavoriteToggle: (Long) -> Unit,
    modifier: Modifier = Modifier,
) {
    when {
        favorites.isEmpty() && totalCount == 0 -> {
            FavoritesEmptyState(modifier = modifier)
        }

        favorites.isEmpty() -> {
            FavoritesMessage(
                text = stringResource(Res.string.favorites_empty_category),
                modifier = modifier,
            )
        }

        else -> {
            LazyColumn(
                modifier = modifier.fillMaxWidth(),
                contentPadding = PaddingValues(start = 16.dp, end = 16.dp, top = 14.dp, bottom = 16.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp),
            ) {
                itemsIndexed(favorites, key = { _, spot -> spot.id }) { index, spot ->
                    SpotDetailCard(
                        spot = spot,
                        isFavorite = true,
                        markerIndex = index + 1,
                        onDirections = onDirections,
                        onFavoriteToggle = onFavoriteToggle,
                        highlighted = true,
                    )
                }
            }
        }
    }
}

@Composable
private fun FavoritesEmptyState(modifier: Modifier = Modifier) {
    val colors = MaterialTheme.colorScheme

    Box(
        modifier = modifier
            .fillMaxWidth()
            .padding(24.dp),
        contentAlignment = Alignment.Center,
    ) {
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Text(text = "♡", fontSize = 42.sp, color = colors.onSurfaceVariant)
            Spacer(modifier = Modifier.height(12.dp))
            Text(
                text = stringResource(Res.string.favorites_empty),
                color = colors.onSurface,
                fontWeight = FontWeight.SemiBold,
            )
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = stringResource(Res.string.favorites_empty_hint),
                color = colors.onSurfaceVariant,
                textAlign = TextAlign.Center,
                style = MaterialTheme.typography.bodyMedium,
            )
        }
    }
}

@Composable
private fun FavoritesMessage(
    text: String,
    modifier: Modifier = Modifier,
) {
    val colors = MaterialTheme.colorScheme

    Box(
        modifier = modifier
            .fillMaxWidth()
            .padding(24.dp),
        contentAlignment = Alignment.Center,
    ) {
        Text(text = text, color = colors.onSurfaceVariant)
    }
}
