package com.example.spotter.feature.home.presentation.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.LoadingIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.luminance
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.spotter.core.designsystem.component.spotterStatusBarsPadding
import com.example.spotter.core.designsystem.theme.SpotterBlue
import com.example.spotter.core.spotui.SpotterTab
import com.example.spotter.core.spotui.component.CarbonFiberBackground
import com.example.spotter.core.spotui.component.SpotCategoryChips
import com.example.spotter.core.spotui.component.SpotSearchBar
import com.example.spotter.core.spotui.component.SpotSearchSuggestionsPanel
import com.example.spotter.core.spotui.component.SpotExpandedCardOverlay
import com.example.spotter.core.spotui.component.SpotCardBounds
import com.example.spotter.core.spotui.component.SpotSpotsList
import com.example.spotter.core.spotui.component.SpotterBottomBar
import com.example.spotter.core.spotui.component.rememberScrollAwareBottomBarState
import com.example.spotter.core.datastore.DefaultHomeViewMode
import com.example.spotter.feature.home.presentation.ui.components.SpotExpandedMapPreview
import com.example.spotter.feature.home.domain.model.SpotDto
import com.example.spotter.feature.map.presentation.platform.MapBackHandler
import com.example.spotter.feature.home.presentation.generated.resources.Res
import com.example.spotter.feature.home.presentation.generated.resources.home_empty_category
import com.example.spotter.feature.home.presentation.generated.resources.home_empty_spots
import com.example.spotter.feature.home.presentation.generated.resources.home_error_generic
import com.example.spotter.feature.home.presentation.generated.resources.home_error_network
import com.example.spotter.feature.home.presentation.generated.resources.home_error_rate_limit
import com.example.spotter.feature.home.presentation.generated.resources.home_error_timeout
import com.example.spotter.feature.home.presentation.generated.resources.home_error_unavailable
import com.example.spotter.feature.home.presentation.generated.resources.home_loading_spots
import com.example.spotter.feature.home.presentation.generated.resources.home_retry
import com.example.spotter.feature.home.presentation.generated.resources.home_search_placeholder
import org.jetbrains.compose.resources.stringResource
import org.koin.compose.viewmodel.koinViewModel

@Composable
fun HomeScreen(
    viewModel: HomeViewModel = koinViewModel(),
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    val colors = MaterialTheme.colorScheme
    val isDark = colors.background.luminance() < 0.5f

    CarbonFiberBackground(modifier = Modifier.fillMaxSize()) {
        Box(modifier = Modifier.fillMaxSize()) {
            val successState = uiState as? HomeUiState.Success
            val barState = rememberScrollAwareBottomBarState()
            val backgroundScale by animateFloatAsState(
                targetValue = if (successState?.isExpandedSpotVisible == true) 0.96f else 1f,
                animationSpec = tween(durationMillis = 520),
                label = "homeBackgroundScale",
            )

            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .nestedScroll(barState.nestedScrollConnection)
                    .graphicsLayer {
                        scaleX = backgroundScale
                        scaleY = backgroundScale
                    }
                    .then(if (isDark) Modifier else Modifier.background(colors.background))
                    .spotterStatusBarsPadding(),
            ) {
            when (val state = uiState) {
                HomeUiState.Loading -> {
                    SpotSearchBar(
                        query = "",
                        onQueryChange = {},
                        placeholder = stringResource(Res.string.home_search_placeholder),
                        enabled = false,
                    )
                    Box(
                        modifier = Modifier.weight(1f),
                        contentAlignment = Alignment.Center,
                    ) {
                        LoadingIndicator(color = SpotterBlue)
                    }
                }

                is HomeUiState.Error -> {
                    SpotSearchBar(
                        query = "",
                        onQueryChange = {},
                        placeholder = stringResource(Res.string.home_search_placeholder),
                        enabled = false,
                    )
                    HomeError(
                        text = mapHomeErrorMessage(state.message),
                        onRetry = viewModel::retry,
                        modifier = Modifier.weight(1f),
                    )
                }

                is HomeUiState.Success -> {
                    HomeSearchContent(
                        state = state,
                        actions = viewModel,
                        onNavigate = viewModel::onNavigateToSpot,
                        modifier = Modifier.weight(1f),
                    )
                }
            }
            }

            SpotterBottomBar(
                selected = SpotterTab.Search,
                onSelected = viewModel::onTabSelected,
                visible = barState.visible && successState?.isExpandedSpotVisible != true,
                modifier = Modifier.align(Alignment.BottomCenter),
            )

            if (successState != null) {
                MapBackHandler(
                    enabled = successState.isExpandedSpotVisible,
                    onBack = viewModel::onExpandedSpotDismiss,
                )

                val expandedSpot = successState.spots.find { it.id == successState.expandedSpotId }

                SpotExpandedCardOverlay(
                    visible = successState.isExpandedSpotVisible,
                    sourceBounds = successState.expandedSpotSourceBounds,
                    spot = expandedSpot,
                    isFavorite = successState.expandedSpotId in successState.favoriteIds,
                    onNavigate = viewModel::onNavigateToSpot,
                    onFavoriteToggle = viewModel::onFavoriteToggle,
                    onDismiss = viewModel::onExpandedSpotDismiss,
                    onExitAnimationEnd = viewModel::onExpandedSpotDismissAnimationEnd,
                    mapContent = { spot ->
                        SpotExpandedMapPreview(
                            spot = spot,
                            userLatitude = successState.userLatitude,
                            userLongitude = successState.userLongitude,
                            routeGeometry = successState.expandedRouteGeometry,
                            isRouteLoading = successState.isExpandedRouteLoading,
                            modifier = Modifier.fillMaxSize(),
                        )
                    },
                )
            }
        }
    }
}

@Composable
private fun HomeSearchContent(
    state: HomeUiState.Success,
    actions: HomeActions,
    onNavigate: (Long) -> Unit,
    modifier: Modifier = Modifier,
) {
    Column(modifier = modifier.fillMaxWidth()) {
        SpotSearchBar(
            query = state.searchText,
            onQueryChange = actions::onSearchTextChanged,
            placeholder = stringResource(Res.string.home_search_placeholder),
            onFocusChanged = actions::onSearchFocusChanged,
            onClear = actions::onSearchClear,
        )

        if (state.isSearchActive) {
            SpotSearchSuggestionsPanel(
                suggestions = state.searchSuggestions,
                onSuggestionSelected = actions::onSearchSuggestionSelected,
            )
            Box(modifier = Modifier.weight(1f))
        } else {
            Spacer(modifier = Modifier.height(12.dp))
            SpotCategoryChips(
                categories = state.categories,
                selectedCategory = state.selectedCategory,
                onCategorySelected = actions::onCategorySelected,
            )

            HomeListContent(
                spots = state.filteredSpots,
                totalSpotsCount = state.spots.size,
                isLoadingSpots = state.isLoadingSpots,
                selectedSpotId = state.selectedSpotId,
                favoriteIds = state.favoriteIds,
                listLayoutMode = state.listLayoutMode,
                onFavoriteToggle = actions::onFavoriteToggle,
                onNavigate = onNavigate,
                onSpotClick = actions::onSpotCardClick,
                expandedSpotId = state.expandedSpotId,
                onRetry = actions::retry,
                modifier = Modifier.weight(1f),
            )
        }
    }
}

@Composable
private fun HomeListContent(
    spots: List<SpotDto>,
    totalSpotsCount: Int,
    isLoadingSpots: Boolean,
    selectedSpotId: Long?,
    favoriteIds: Set<Long>,
    listLayoutMode: DefaultHomeViewMode,
    onFavoriteToggle: (Long) -> Unit,
    onNavigate: (Long) -> Unit,
    onSpotClick: (Long, SpotCardBounds) -> Unit,
    expandedSpotId: Long? = null,
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

    SpotSpotsList(
        spots = spots,
        layoutMode = listLayoutMode,
        favoriteIds = favoriteIds,
        selectedSpotId = selectedSpotId,
        onNavigate = onNavigate,
        onFavoriteToggle = onFavoriteToggle,
        onSpotClick = onSpotClick,
        expandedSpotId = expandedSpotId,
        modifier = modifier,
    )
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

        message.contains("Failed to connect", ignoreCase = true) ||
            message.contains("Connection refused", ignoreCase = true) ||
            message.contains("Unable to resolve host", ignoreCase = true) ||
            message.contains("Network is unreachable", ignoreCase = true) ->
            stringResource(Res.string.home_error_network)

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
