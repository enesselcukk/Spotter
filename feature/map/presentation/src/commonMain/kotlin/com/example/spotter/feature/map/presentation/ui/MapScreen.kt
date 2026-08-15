package com.example.spotter.feature.map.presentation.ui

import androidx.compose.animation.AnimatedVisibility
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
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.spotter.core.designsystem.theme.SpotterBlue
import com.example.spotter.core.designsystem.theme.SpotterYellow
import com.example.spotter.feature.home.domain.util.GeoDistance
import com.example.spotter.feature.map.contract.MapScreenDestination
import com.example.spotter.feature.map.domain.model.TravelMode
import com.example.spotter.feature.map.domain.util.RouteFormatter
import com.example.spotter.feature.map.presentation.generated.resources.Res
import com.example.spotter.feature.map.presentation.generated.resources.map_duration_hours
import com.example.spotter.feature.map.presentation.generated.resources.map_duration_minutes
import com.example.spotter.feature.map.presentation.generated.resources.map_duration_seconds
import com.example.spotter.feature.map.presentation.generated.resources.map_mode_cycling
import com.example.spotter.feature.map.presentation.generated.resources.map_mode_driving
import com.example.spotter.feature.map.presentation.generated.resources.map_mode_walking
import com.example.spotter.feature.map.presentation.generated.resources.map_no_selection
import com.example.spotter.feature.map.presentation.generated.resources.map_retry
import com.example.spotter.feature.map.presentation.generated.resources.map_route_error
import com.example.spotter.feature.map.presentation.generated.resources.map_route_loading
import com.example.spotter.feature.map.presentation.generated.resources.map_start_navigation
import com.example.spotter.feature.map.presentation.generated.resources.map_steps_hide
import com.example.spotter.feature.map.presentation.generated.resources.map_steps_show
import com.example.spotter.feature.map.presentation.generated.resources.map_unnamed_spot
import com.example.spotter.feature.map.presentation.map.MapCamera
import com.example.spotter.feature.map.presentation.map.RouteMapState
import com.example.spotter.feature.map.presentation.map.RouteMapView
import com.example.spotter.feature.map.presentation.map.rememberRouteMapState
import com.example.spotter.feature.map.presentation.platform.MapBackHandler
import com.example.spotter.feature.map.presentation.platform.rememberTurnByTurnLauncher
import org.jetbrains.compose.resources.stringResource
import org.koin.compose.viewmodel.koinViewModel

@Composable
fun MapScreen(
    destination: MapScreenDestination,
    viewModel: MapViewModel = koinViewModel(),
) {
    LaunchedEffect(destination) {
        viewModel.start(destination)
    }

    val state by viewModel.uiState.collectAsStateWithLifecycle()
    val mapState = rememberRouteMapState()
    val startNavigation = rememberTurnByTurnLauncher()

    MapBackHandler(enabled = true, onBack = viewModel::onBack)

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background),
    ) {
        RouteMapView(
            userLocation = state.userLocation,
            spots = state.spots,
            selectedSpotId = state.selectedSpotId,
            routeGeometry = state.routeGeometry,
            mapState = mapState,
            onSpotSelected = viewModel::onSpotSelected,
            modifier = Modifier.fillMaxSize(),
        )

        MapTopBar(
            state = state,
            onBack = viewModel::onBack,
            modifier = Modifier.align(Alignment.TopCenter),
        )

        MapCameraControls(
            mapState = mapState,
            hasRoute = state.routeGeometry.size > 1,
            modifier = Modifier
                .align(Alignment.CenterEnd)
                .padding(end = 16.dp),
        )

        RouteSheet(
            state = state,
            actions = viewModel,
            onStartNavigation = {
                state.selectedSpot?.let { spot ->
                    startNavigation(spot.lat, spot.lon, spot.name)
                }
            },
            modifier = Modifier.align(Alignment.BottomCenter),
        )
    }
}

@Composable
private fun MapTopBar(
    state: MapUiState,
    onBack: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val colors = MaterialTheme.colorScheme
    val spot = state.selectedSpot

    Row(
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 14.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(12.dp),
    ) {
        Box(
            modifier = Modifier
                .size(46.dp)
                .clip(CircleShape)
                .background(colors.surface)
                .clickable(onClick = onBack),
            contentAlignment = Alignment.Center,
        ) {
            Text(text = "←", color = colors.onSurface, fontSize = 20.sp, fontWeight = FontWeight.Bold)
        }

        Column(
            modifier = Modifier
                .weight(1f)
                .clip(RoundedCornerShape(24.dp))
                .background(colors.surface)
                .padding(horizontal = 18.dp, vertical = 10.dp),
        ) {
            Text(
                text = spot?.name?.takeIf { it.isNotBlank() }
                    ?: stringResource(Res.string.map_unnamed_spot),
                color = colors.onSurface,
                fontSize = 15.sp,
                fontWeight = FontWeight.Bold,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
            )
            Text(
                text = spot
                    ?.let { RouteFormatter.formatCoordinates(it.lat, it.lon) }
                    ?: RouteFormatter.formatCoordinates(
                        state.userLocation.latitude,
                        state.userLocation.longitude,
                    ),
                color = colors.onSurfaceVariant,
                fontSize = 12.sp,
                maxLines = 1,
            )
        }
    }
}

@Composable
private fun MapCameraControls(
    mapState: RouteMapState,
    hasRoute: Boolean,
    modifier: Modifier = Modifier,
) {
    Column(
        modifier = modifier,
        verticalArrangement = Arrangement.spacedBy(10.dp),
    ) {
        CircleMapButton(glyph = "＋", onClick = { mapState.send(MapCamera.ZoomIn) })
        CircleMapButton(glyph = "－", onClick = { mapState.send(MapCamera.ZoomOut) })
        CircleMapButton(glyph = "◎", onClick = { mapState.send(MapCamera.FollowUser) })
        if (hasRoute) {
            CircleMapButton(glyph = "⤢", onClick = { mapState.send(MapCamera.FitRoute) })
        }
    }
}

@Composable
private fun CircleMapButton(
    glyph: String,
    onClick: () -> Unit,
) {
    val colors = MaterialTheme.colorScheme

    Box(
        modifier = Modifier
            .size(44.dp)
            .clip(CircleShape)
            .background(colors.surface)
            .border(1.dp, colors.outline.copy(alpha = 0.25f), CircleShape)
            .clickable(onClick = onClick),
        contentAlignment = Alignment.Center,
    ) {
        Text(
            text = glyph,
            color = colors.onSurface,
            fontSize = 18.sp,
            fontWeight = FontWeight.Bold,
        )
    }
}

@Composable
private fun RouteSheet(
    state: MapUiState,
    actions: MapActions,
    onStartNavigation: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val colors = MaterialTheme.colorScheme

    Column(
        modifier = modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(topStart = 28.dp, topEnd = 28.dp))
            .background(colors.surface)
            .padding(horizontal = 20.dp, vertical = 16.dp),
        verticalArrangement = Arrangement.spacedBy(14.dp),
    ) {
        Box(
            modifier = Modifier
                .align(Alignment.CenterHorizontally)
                .width(40.dp)
                .height(4.dp)
                .clip(RoundedCornerShape(2.dp))
                .background(colors.onSurfaceVariant.copy(alpha = 0.35f)),
        )

        TravelModeSelector(
            selected = state.travelMode,
            onSelected = actions::onTravelModeSelected,
        )

        when {
            state.selectedSpot == null -> SheetMessage(text = stringResource(Res.string.map_no_selection))

            state.isRouteLoading -> Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(12.dp),
            ) {
                CircularProgressIndicator(
                    modifier = Modifier.size(20.dp),
                    strokeWidth = 2.dp,
                    color = SpotterBlue,
                )
                Text(
                    text = stringResource(Res.string.map_route_loading),
                    color = colors.onSurfaceVariant,
                    fontSize = 14.sp,
                )
            }

            state.hasRouteError -> Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                SheetMessage(text = stringResource(Res.string.map_route_error))
                Button(
                    onClick = actions::onRetryRoute,
                    shape = RoundedCornerShape(24.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = SpotterBlue,
                        contentColor = Color.White,
                    ),
                ) {
                    Text(stringResource(Res.string.map_retry))
                }
            }

            state.routePlan != null -> RouteSummary(
                state = state,
                actions = actions,
                onStartNavigation = onStartNavigation,
            )
        }
    }
}

@Composable
private fun RouteSummary(
    state: MapUiState,
    actions: MapActions,
    onStartNavigation: () -> Unit,
) {
    val colors = MaterialTheme.colorScheme
    val plan = state.routePlan ?: return

    val duration = RouteFormatter.formatDuration(
        seconds = plan.durationSeconds,
        secondsSuffix = stringResource(Res.string.map_duration_seconds),
        minutesSuffix = stringResource(Res.string.map_duration_minutes),
        hoursSuffix = stringResource(Res.string.map_duration_hours),
    )

    Column(verticalArrangement = Arrangement.spacedBy(14.dp)) {
        Row(verticalAlignment = Alignment.Bottom) {
            Text(
                text = duration,
                color = colors.onSurface,
                fontSize = 28.sp,
                fontWeight = FontWeight.Bold,
            )
            Spacer(modifier = Modifier.width(10.dp))
            Text(
                text = GeoDistance.formatDistance(plan.distanceMeters, state.distanceUnit),
                color = colors.onSurfaceVariant,
                fontSize = 16.sp,
                modifier = Modifier.padding(bottom = 3.dp),
            )
        }

        Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
            Button(
                onClick = onStartNavigation,
                modifier = Modifier
                    .weight(1f)
                    .height(50.dp),
                shape = RoundedCornerShape(26.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = SpotterYellow,
                    contentColor = Color.Black,
                ),
                elevation = ButtonDefaults.buttonElevation(defaultElevation = 0.dp),
            ) {
                Text(
                    text = "➤  ${stringResource(Res.string.map_start_navigation)}",
                    fontWeight = FontWeight.Bold,
                    fontSize = 15.sp,
                )
            }

            Box(
                modifier = Modifier
                    .height(50.dp)
                    .clip(RoundedCornerShape(26.dp))
                    .background(colors.surfaceVariant)
                    .clickable(onClick = actions::onStepsToggle)
                    .padding(horizontal = 20.dp),
                contentAlignment = Alignment.Center,
            ) {
                Text(
                    text = if (state.areStepsExpanded) {
                        stringResource(Res.string.map_steps_hide)
                    } else {
                        stringResource(Res.string.map_steps_show)
                    },
                    color = colors.onSurfaceVariant,
                    fontWeight = FontWeight.Medium,
                    fontSize = 14.sp,
                )
            }
        }

        AnimatedVisibility(visible = state.areStepsExpanded) {
            LazyColumn(
                modifier = Modifier.heightIn(max = 260.dp),
                contentPadding = PaddingValues(vertical = 4.dp),
                verticalArrangement = Arrangement.spacedBy(2.dp),
            ) {
                itemsIndexed(plan.steps) { index, step ->
                    RouteStepRow(
                        glyph = routeStepGlyph(step),
                        instruction = routeStepInstruction(step),
                        distance = GeoDistance.formatDistance(step.distanceMeters, state.distanceUnit),
                        isLast = index == plan.steps.lastIndex,
                    )
                }
            }
        }
    }
}

@Composable
private fun RouteStepRow(
    glyph: String,
    instruction: String,
    distance: String,
    isLast: Boolean,
) {
    val colors = MaterialTheme.colorScheme

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 10.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(14.dp),
    ) {
        Text(
            text = glyph,
            color = if (isLast) SpotterYellow else SpotterBlue,
            fontSize = 18.sp,
            modifier = Modifier.width(24.dp),
        )
        Text(
            text = instruction,
            color = colors.onSurface,
            fontSize = 14.sp,
            modifier = Modifier.weight(1f),
        )
        Text(
            text = distance,
            color = colors.onSurfaceVariant,
            fontSize = 12.sp,
        )
    }
}

@Composable
private fun TravelModeSelector(
    selected: TravelMode,
    onSelected: (TravelMode) -> Unit,
) {
    val colors = MaterialTheme.colorScheme

    Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
        TravelMode.entries.forEach { mode ->
            val isSelected = mode == selected
            Row(
                modifier = Modifier
                    .clip(RoundedCornerShape(20.dp))
                    .background(if (isSelected) SpotterBlue else colors.surfaceVariant)
                    .clickable { onSelected(mode) }
                    .padding(horizontal = 16.dp, vertical = 9.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(6.dp),
            ) {
                Text(text = travelModeGlyph(mode), fontSize = 14.sp)
                Text(
                    text = travelModeLabel(mode),
                    color = if (isSelected) Color.White else colors.onSurfaceVariant,
                    fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium,
                    fontSize = 13.sp,
                )
            }
        }
    }
}

@Composable
private fun travelModeLabel(mode: TravelMode): String = when (mode) {
    TravelMode.DRIVING -> stringResource(Res.string.map_mode_driving)
    TravelMode.CYCLING -> stringResource(Res.string.map_mode_cycling)
    TravelMode.WALKING -> stringResource(Res.string.map_mode_walking)
}

private fun travelModeGlyph(mode: TravelMode): String = when (mode) {
    TravelMode.DRIVING -> "🚗"
    TravelMode.CYCLING -> "🚲"
    TravelMode.WALKING -> "🚶"
}

@Composable
private fun SheetMessage(text: String) {
    Text(
        text = text,
        color = MaterialTheme.colorScheme.onSurfaceVariant,
        fontSize = 14.sp,
    )
}
