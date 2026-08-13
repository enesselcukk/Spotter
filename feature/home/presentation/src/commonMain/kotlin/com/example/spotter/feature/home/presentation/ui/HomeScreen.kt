package com.example.spotter.feature.home.presentation.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.LoadingIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.luminance
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.spotter.core.designsystem.theme.SpotterBlue
import com.example.spotter.core.designsystem.theme.SpotterYellow
import com.example.spotter.feature.home.domain.model.SpotDto
import com.example.spotter.feature.home.presentation.generated.resources.Res
import com.example.spotter.feature.home.presentation.generated.resources.*
import com.example.spotter.feature.home.presentation.map.SpotMapView
import com.example.spotter.feature.home.presentation.platform.MapBackHandler
import com.example.spotter.feature.home.presentation.platform.rememberDirectionsLauncher
import com.example.spotter.feature.home.presentation.ui.components.HomeBrandingHeader
import com.example.spotter.feature.home.presentation.ui.components.SpotCompactItem
import com.example.spotter.feature.home.presentation.ui.components.SpotDetailCard
import com.example.spotter.feature.home.presentation.ui.components.SpotterBottomBar
import org.jetbrains.compose.resources.stringResource
import org.koin.compose.viewmodel.koinViewModel

@Composable
fun HomeScreen(
    viewModel: HomeViewModel = koinViewModel(),
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    val launchDirections = rememberDirectionsLauncher()
    val colors = MaterialTheme.colorScheme

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(colors.background),
    ) {
        when (val state = uiState) {
            HomeUiState.Loading -> {
                HomeBrandingHeader(locationLabel = null, usesDeviceLocation = false)
                Box(
                    modifier = Modifier.weight(1f),
                    contentAlignment = Alignment.Center,
                ) {
                    LoadingIndicator(color = SpotterBlue)
                }
            }

            is HomeUiState.Error -> {
                HomeBrandingHeader(locationLabel = null, usesDeviceLocation = false)
                HomeError(
                    text = mapHomeErrorMessage(state.message),
                    onRetry = viewModel::retry,
                    modifier = Modifier.weight(1f),
                )
            }

            is HomeUiState.Success -> {
                if (state.selectedBottomNav != HomeBottomNav.Settings) {
                    HomeBrandingHeader(
                        locationLabel = state.locationLabel,
                        usesDeviceLocation = state.usesDeviceLocation,
                    )
                }

                when (state.selectedBottomNav) {
                    HomeBottomNav.Search -> HomeSearchContent(
                        state = state,
                        actions = viewModel,
                        onDirections = launchDirections,
                        modifier = Modifier.weight(1f),
                    )

                    HomeBottomNav.Favorites -> HomeFavoritesContent(
                        favorites = state.favoriteSpots,
                        favoriteIds = state.favoriteIds,
                        onDirections = launchDirections,
                        onFavoriteToggle = viewModel::onFavoriteToggle,
                        modifier = Modifier.weight(1f),
                    )

                    HomeBottomNav.Settings -> SettingsScreen(
                        modifier = Modifier.weight(1f),
                    )
                }

                SpotterBottomBar(
                    selected = state.selectedBottomNav,
                    onSelected = viewModel::onBottomNavSelected,
                )
            }
        }
    }
}

@Composable
private fun HomeSearchContent(
    state: HomeUiState.Success,
    actions: HomeActions,
    onDirections: (Double, Double, String?) -> Unit,
    modifier: Modifier = Modifier,
) {
    Column(modifier = modifier.fillMaxWidth()) {
        HomeCategoryChips(
            categories = state.categories,
            selectedCategory = state.selectedCategory,
            onCategorySelected = actions::onCategorySelected,
        )
        HomeViewToggle(
            viewMode = state.viewMode,
            onToggle = actions::onViewModeToggle,
        )

        Box(modifier = Modifier.weight(1f)) {
            when (state.viewMode) {
                HomeViewMode.List -> HomeListContent(
                    spots = state.filteredSpots,
                    totalSpotsCount = state.spots.size,
                    selectedCategory = state.selectedCategory,
                    isLoadingSpots = state.isLoadingSpots,
                    selectedSpotId = state.selectedSpotId,
                    favoriteIds = state.favoriteIds,
                    onSpotSelected = actions::onSpotSelected,
                    onFavoriteToggle = actions::onFavoriteToggle,
                    onDirections = onDirections,
                    onRetry = actions::retry,
                    modifier = Modifier.fillMaxSize(),
                )

                HomeViewMode.Map -> {
                    SpotMapView(
                        spots = state.filteredSpots,
                        selectedSpotId = state.selectedSpotId,
                        userLatitude = state.userLatitude,
                        userLongitude = state.userLongitude,
                        onSpotSelected = actions::onSpotSelected,
                        modifier = Modifier.fillMaxSize(),
                    )
                    MapBackHandler(enabled = true, onBack = actions::onViewModeToggle)
                    Column(
                        modifier = Modifier
                            .align(Alignment.BottomCenter)
                            .fillMaxWidth()
                            .padding(horizontal = 20.dp, vertical = 16.dp),
                        verticalArrangement = Arrangement.spacedBy(10.dp),
                    ) {
                        state.selectedSpot?.let { spot ->
                            SpotDetailCard(
                                spot = spot,
                                isFavorite = spot.id in state.favoriteIds,
                                onDirections = onDirections,
                                onFavoriteToggle = actions::onFavoriteToggle,
                                highlighted = true,
                            )
                        } ?: state.filteredSpots.take(2).forEach { spot ->
                            SpotCompactItem(
                                spot = spot,
                                onClick = { actions.onSpotSelected(spot.id) },
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun HomeCategoryChips(
    categories: List<String>,
    selectedCategory: String,
    onCategorySelected: (String) -> Unit,
) {
    val colors = MaterialTheme.colorScheme
    val isDark = colors.background.luminance() < 0.5f

    LazyRow(
        contentPadding = PaddingValues(horizontal = 20.dp),
        horizontalArrangement = Arrangement.spacedBy(10.dp),
    ) {
        items(categories) { category ->
            val selected = category == selectedCategory
            val chipBackground = when {
                selected -> if (isDark) SpotterBlue.copy(alpha = 0.85f) else colors.primaryContainer
                else -> if (isDark) colors.surface else colors.surfaceVariant
            }
            val chipBorder = when {
                selected -> Color.Transparent
                else -> colors.outline.copy(alpha = if (isDark) 0.35f else 0.5f)
            }
            val chipText = when {
                selected -> if (isDark) Color.White else colors.onPrimaryContainer
                else -> colors.onSurfaceVariant
            }

            Row(
                modifier = Modifier
                    .clip(RoundedCornerShape(28.dp))
                    .background(chipBackground)
                    .border(1.dp, chipBorder, RoundedCornerShape(28.dp))
                    .clickable { onCategorySelected(category) }
                    .padding(horizontal = 18.dp, vertical = 11.dp),
                verticalAlignment = Alignment.CenterVertically,
            ) {
                Text(text = HomeCategories.icon(category), fontSize = 16.sp)
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = homeCategoryLabel(category),
                    color = chipText,
                    fontWeight = if (selected) FontWeight.Bold else FontWeight.Medium,
                    fontSize = 14.sp,
                )
            }
        }
    }
}

@Composable
private fun HomeViewToggle(
    viewMode: HomeViewMode,
    onToggle: () -> Unit,
) {
    Button(
        onClick = onToggle,
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 20.dp, vertical = 16.dp)
            .height(52.dp),
        shape = RoundedCornerShape(28.dp),
        colors = ButtonDefaults.buttonColors(
            containerColor = SpotterYellow,
            contentColor = Color.Black,
        ),
        elevation = ButtonDefaults.buttonElevation(defaultElevation = 0.dp),
    ) {
        Text(
            text = if (viewMode == HomeViewMode.List) {
                "🗺  ${stringResource(Res.string.home_map_view)}"
            } else {
                "☰  ${stringResource(Res.string.home_list_view)}"
            },
            fontWeight = FontWeight.Bold,
            fontSize = 16.sp,
        )
    }
}

@Composable
private fun HomeListContent(
    spots: List<SpotDto>,
    totalSpotsCount: Int,
    selectedCategory: String,
    isLoadingSpots: Boolean,
    selectedSpotId: Long?,
    favoriteIds: Set<Long>,
    onSpotSelected: (Long) -> Unit,
    onFavoriteToggle: (Long) -> Unit,
    onDirections: (Double, Double, String?) -> Unit,
    onRetry: () -> Unit,
    modifier: Modifier = Modifier,
) {
    if (spots.isEmpty()) {
        when {
            isLoadingSpots -> {
                Box(
                    modifier = modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center,
                ) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        LoadingIndicator(color = SpotterBlue)
                        Spacer(modifier = Modifier.height(12.dp))
                        Text(
                            text = stringResource(Res.string.home_loading_spots),
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                        )
                    }
                }
            }

            totalSpotsCount > 0 -> {
                HomeMessage(
                    text = stringResource(Res.string.home_empty_category),
                    modifier = modifier,
                )
            }

            else -> {
                Box(
                    modifier = modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center,
                ) {
                    HomeEmptyWithRetry(onRetry = onRetry)
                }
            }
        }
        return
    }

    val listState = rememberLazyListState()
    val selectedSpot = spots.find { it.id == selectedSpotId } ?: spots.first()

    LaunchedEffect(selectedSpot.id) {
        listState.animateScrollToItem(0)
    }

    LazyColumn(
        modifier = modifier.fillMaxWidth(),
        state = listState,
        contentPadding = PaddingValues(start = 20.dp, end = 20.dp, bottom = 8.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp),
    ) {
        item(key = "selected-${selectedSpot.id}") {
            SpotDetailCard(
                spot = selectedSpot,
                isFavorite = selectedSpot.id in favoriteIds,
                markerIndex = 1,
                onDirections = onDirections,
                onFavoriteToggle = onFavoriteToggle,
                highlighted = true,
            )
        }

        itemsIndexed(
            items = spots.filter { it.id != selectedSpot.id },
            key = { _, spot -> spot.id },
        ) { index, spot ->
            SpotCompactItem(
                spot = spot,
                onClick = { onSpotSelected(spot.id) },
            )
        }
    }
}

@Composable
private fun HomeFavoritesContent(
    favorites: List<SpotDto>,
    favoriteIds: Set<Long>,
    onDirections: (Double, Double, String?) -> Unit,
    onFavoriteToggle: (Long) -> Unit,
    modifier: Modifier = Modifier,
) {
    if (favorites.isEmpty()) {
        HomeMessage(
            text = stringResource(Res.string.home_empty_favorites),
            modifier = modifier,
        )
        return
    }

    LazyColumn(
        modifier = modifier.fillMaxWidth(),
        contentPadding = PaddingValues(20.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp),
    ) {
        itemsIndexed(favorites, key = { _, spot -> spot.id }) { index, spot ->
            SpotDetailCard(
                spot = spot,
                isFavorite = spot.id in favoriteIds,
                markerIndex = index + 1,
                onDirections = onDirections,
                onFavoriteToggle = onFavoriteToggle,
                highlighted = true,
            )
        }
    }
}

@Composable
private fun mapHomeErrorMessage(message: String?): String {
    val fallback = stringResource(Res.string.home_error_generic)
    if (message.isNullOrBlank()) return fallback
    return when {
        message.contains("Gateway Timeout", ignoreCase = true) ||
            message.contains("504", ignoreCase = true) ||
            message.contains("timeout", ignoreCase = true) ->
            stringResource(Res.string.home_error_timeout)

        message.contains("429", ignoreCase = true) ||
            message.contains("Too Many Requests", ignoreCase = true) ->
            stringResource(Res.string.home_error_rate_limit)

        message.contains("503", ignoreCase = true) ||
            message.contains("502", ignoreCase = true) ||
            message.contains("Unavailable", ignoreCase = true) ->
            stringResource(Res.string.home_error_unavailable)

        else -> message
    }
}

@Composable
private fun HomeError(
    text: String,
    onRetry: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val colors = MaterialTheme.colorScheme

    Column(
        modifier = modifier
            .fillMaxWidth()
            .padding(24.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center,
    ) {
        Text(text = text, color = colors.onSurfaceVariant)
        Spacer(modifier = Modifier.height(12.dp))
        Button(
            onClick = onRetry,
            shape = RoundedCornerShape(24.dp),
            colors = ButtonDefaults.buttonColors(
                containerColor = SpotterBlue,
                contentColor = Color.White,
            ),
        ) {
            Text(stringResource(Res.string.home_retry))
        }
    }
}

@Composable
private fun HomeEmptyWithRetry(
    onRetry: () -> Unit,
) {
    val colors = MaterialTheme.colorScheme

    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        Text(
            text = stringResource(Res.string.home_empty_spots),
            color = colors.onSurfaceVariant,
        )
        Spacer(modifier = Modifier.height(12.dp))
        Button(
            onClick = onRetry,
            shape = RoundedCornerShape(24.dp),
            colors = ButtonDefaults.buttonColors(
                containerColor = SpotterBlue,
                contentColor = Color.White,
            ),
        ) {
            Text(stringResource(Res.string.home_retry))
        }
    }
}

@Composable
private fun HomeMessage(
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
