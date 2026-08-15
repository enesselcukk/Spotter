package com.example.spotter.feature.map.presentation.ui

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.Canvas
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
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.spotter.core.designsystem.component.spotterNavigationBarsPadding
import com.example.spotter.core.designsystem.component.spotterStatusBarsPadding
import com.example.spotter.feature.home.domain.util.GeoDistance
import com.example.spotter.feature.map.contract.MapScreenDestination
import com.example.spotter.feature.map.domain.model.TravelMode
import com.example.spotter.feature.map.domain.util.RouteFormatter
import com.example.spotter.feature.map.presentation.generated.resources.Res
import com.example.spotter.feature.map.presentation.generated.resources.map_back
import com.example.spotter.feature.map.presentation.generated.resources.map_duration_hours
import com.example.spotter.feature.map.presentation.generated.resources.map_duration_minutes
import com.example.spotter.feature.map.presentation.generated.resources.map_duration_seconds
import com.example.spotter.feature.map.presentation.generated.resources.map_fit_route
import com.example.spotter.feature.map.presentation.generated.resources.map_mode_cycling
import com.example.spotter.feature.map.presentation.generated.resources.map_mode_driving
import com.example.spotter.feature.map.presentation.generated.resources.map_mode_walking
import com.example.spotter.feature.map.presentation.generated.resources.map_no_selection
import com.example.spotter.feature.map.presentation.generated.resources.map_recenter
import com.example.spotter.feature.map.presentation.generated.resources.map_retry
import com.example.spotter.feature.map.presentation.generated.resources.map_route_error
import com.example.spotter.feature.map.presentation.generated.resources.map_route_loading
import com.example.spotter.feature.map.presentation.generated.resources.map_start_navigation
import com.example.spotter.feature.map.presentation.generated.resources.map_stop_navigation
import com.example.spotter.feature.map.presentation.generated.resources.map_steps_hide
import com.example.spotter.feature.map.presentation.generated.resources.map_steps_show
import com.example.spotter.feature.map.presentation.generated.resources.map_unnamed_spot
import com.example.spotter.feature.map.presentation.generated.resources.map_you_have_arrived
import com.example.spotter.feature.map.presentation.generated.resources.map_zoom_in
import com.example.spotter.feature.map.presentation.generated.resources.map_zoom_out
import com.example.spotter.feature.map.presentation.map.MapCamera
import com.example.spotter.feature.map.presentation.map.RouteMapState
import com.example.spotter.feature.map.presentation.map.RouteMapView
import com.example.spotter.feature.map.presentation.map.rememberRouteMapState
import com.example.spotter.feature.map.presentation.platform.MapBackHandler
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
            followUser = state.isNavigating,
            modifier = Modifier.fillMaxSize(),
        )

        if (state.isNavigating) {
            NavigationBanner(
                state = state,
                onStop = viewModel::onStopNavigation,
                modifier = Modifier
                    .align(Alignment.TopCenter)
                    .spotterStatusBarsPadding(),
            )
        } else {
            MapTopBar(
                state = state,
                onBack = viewModel::onBack,
                modifier = Modifier
                    .align(Alignment.TopCenter)
                    .spotterStatusBarsPadding(),
            )
        }

        MapCameraControls(
            mapState = mapState,
            hasRoute = state.routeGeometry.size > 1 && !state.isNavigating,
            modifier = Modifier
                .align(Alignment.CenterEnd)
                .padding(end = 16.dp),
        )

        if (state.isNavigating) {
            NavigationSheet(
                state = state,
                onStop = viewModel::onStopNavigation,
                modifier = Modifier.align(Alignment.BottomCenter),
            )
        } else {
            RouteSheet(
                state = state,
                actions = viewModel,
                onStartNavigation = viewModel::onStartNavigation,
                modifier = Modifier.align(Alignment.BottomCenter),
            )
        }
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
            .padding(horizontal = 16.dp, vertical = 12.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(12.dp),
    ) {
        MapCircleButton(
            contentDescription = stringResource(Res.string.map_back),
            onClick = onBack,
            size = 52.dp,
        ) { tint ->
            BackArrowIcon(tint = tint, modifier = Modifier.size(26.dp))
        }

        Column(
            modifier = Modifier
                .weight(1f)
                .mapSurface(RoundedCornerShape(26.dp))
                .padding(horizontal = 18.dp, vertical = 11.dp),
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
        MapCircleButton(
            contentDescription = stringResource(Res.string.map_zoom_in),
            onClick = { mapState.send(MapCamera.ZoomIn) },
        ) { tint -> MapGlyphIcon(MapGlyph.ZoomIn, tint) }

        MapCircleButton(
            contentDescription = stringResource(Res.string.map_zoom_out),
            onClick = { mapState.send(MapCamera.ZoomOut) },
        ) { tint -> MapGlyphIcon(MapGlyph.ZoomOut, tint) }

        MapCircleButton(
            contentDescription = stringResource(Res.string.map_recenter),
            onClick = { mapState.send(MapCamera.FollowUser) },
        ) { tint -> MapGlyphIcon(MapGlyph.Recenter, tint) }

        if (hasRoute) {
            MapCircleButton(
                contentDescription = stringResource(Res.string.map_fit_route),
                onClick = { mapState.send(MapCamera.FitRoute) },
            ) { tint -> MapGlyphIcon(MapGlyph.FitRoute, tint) }
        }
    }
}

@Composable
private fun MapCircleButton(
    contentDescription: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    size: Dp = 46.dp,
    content: @Composable (tint: Color) -> Unit,
) {
    Box(
        modifier = modifier
            .size(size)
            .mapSurface(CircleShape)
            .clickable(
                onClickLabel = contentDescription,
                role = Role.Button,
                onClick = onClick,
            ),
        contentAlignment = Alignment.Center,
    ) {
        content(MaterialTheme.colorScheme.onSurface)
    }
}

@Composable
private fun Modifier.mapSurface(shape: Shape): Modifier {
    val colors = MaterialTheme.colorScheme

    return this
        .shadow(
            elevation = 8.dp,
            shape = shape,
            ambientColor = Color.Black.copy(alpha = 0.30f),
            spotColor = Color.Black.copy(alpha = 0.22f),
        )
        .clip(shape)
        .background(colors.surface)
        .border(1.dp, colors.outline.copy(alpha = 0.35f), shape)
}

@Composable
private fun BackArrowIcon(
    tint: Color,
    modifier: Modifier = Modifier,
) {
    Canvas(modifier = modifier) {
        val strokeWidth = size.minDimension * 0.095f
        val centerY = size.height / 2f
        val tailX = size.width * 0.78f
        val headX = size.width * 0.24f
        val headArm = size.minDimension * 0.23f

        drawLine(
            color = tint,
            start = Offset(tailX, centerY),
            end = Offset(headX, centerY),
            strokeWidth = strokeWidth,
            cap = StrokeCap.Round,
        )
        drawLine(
            color = tint,
            start = Offset(headX, centerY),
            end = Offset(headX + headArm, centerY - headArm),
            strokeWidth = strokeWidth,
            cap = StrokeCap.Round,
        )
        drawLine(
            color = tint,
            start = Offset(headX, centerY),
            end = Offset(headX + headArm, centerY + headArm),
            strokeWidth = strokeWidth,
            cap = StrokeCap.Round,
        )
    }
}

private enum class MapGlyph {
    ZoomIn,
    ZoomOut,
    Recenter,
    FitRoute,
}

@Composable
private fun MapGlyphIcon(
    glyph: MapGlyph,
    tint: Color,
    modifier: Modifier = Modifier,
) {
    Canvas(modifier = modifier.size(22.dp)) {
        val strokeWidth = size.minDimension * 0.1f
        val centerX = size.width / 2f
        val centerY = size.height / 2f

        fun line(startX: Float, startY: Float, endX: Float, endY: Float) = drawLine(
            color = tint,
            start = Offset(startX, startY),
            end = Offset(endX, endY),
            strokeWidth = strokeWidth,
            cap = StrokeCap.Round,
        )

        when (glyph) {
            MapGlyph.ZoomIn -> {
                val arm = size.minDimension * 0.3f
                line(centerX - arm, centerY, centerX + arm, centerY)
                line(centerX, centerY - arm, centerX, centerY + arm)
            }

            MapGlyph.ZoomOut -> {
                val arm = size.minDimension * 0.3f
                line(centerX - arm, centerY, centerX + arm, centerY)
            }

            MapGlyph.Recenter -> {
                drawCircle(
                    color = tint,
                    radius = size.minDimension * 0.26f,
                    center = Offset(centerX, centerY),
                    style = Stroke(width = strokeWidth),
                )
                drawCircle(
                    color = tint,
                    radius = size.minDimension * 0.08f,
                    center = Offset(centerX, centerY),
                )
                val tickInner = size.minDimension * 0.34f
                val tickOuter = size.minDimension * 0.48f
                line(centerX, centerY - tickInner, centerX, centerY - tickOuter)
                line(centerX, centerY + tickInner, centerX, centerY + tickOuter)
                line(centerX - tickInner, centerY, centerX - tickOuter, centerY)
                line(centerX + tickInner, centerY, centerX + tickOuter, centerY)
            }

            MapGlyph.FitRoute -> {
                val inset = size.minDimension * 0.2f
                val arm = size.minDimension * 0.2f
                val far = size.minDimension - inset
                line(inset, inset + arm, inset, inset)
                line(inset, inset, inset + arm, inset)
                line(far - arm, inset, far, inset)
                line(far, inset, far, inset + arm)
                line(inset, far - arm, inset, far)
                line(inset, far, inset + arm, far)
                line(far - arm, far, far, far)
                line(far, far, far, far - arm)
            }
        }
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
    val sheetShape = RoundedCornerShape(topStart = 28.dp, topEnd = 28.dp)

    Column(
        modifier = modifier
            .fillMaxWidth()
            .shadow(
                elevation = 16.dp,
                shape = sheetShape,
                ambientColor = Color.Black.copy(alpha = 0.30f),
                spotColor = Color.Black.copy(alpha = 0.22f),
            )
            .clip(sheetShape)
            .background(colors.surface)
            .spotterNavigationBarsPadding()
            .padding(start = 20.dp, end = 20.dp, top = 14.dp, bottom = 18.dp),
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
                    color = colors.secondary,
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
                        containerColor = colors.secondary,
                        contentColor = colors.onSecondary,
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
                    containerColor = colors.primary,
                    contentColor = colors.onPrimary,
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
            color = if (isLast) colors.primary else colors.secondary,
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
                    .background(if (isSelected) colors.secondary else colors.surfaceVariant)
                    .clickable(role = Role.RadioButton) { onSelected(mode) }
                    .padding(horizontal = 16.dp, vertical = 9.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(6.dp),
            ) {
                Text(text = travelModeGlyph(mode), fontSize = 14.sp)
                Text(
                    text = travelModeLabel(mode),
                    color = if (isSelected) colors.onSecondary else colors.onSurfaceVariant,
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

@Composable
private fun NavigationBanner(
    state: MapUiState,
    onStop: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val colors = MaterialTheme.colorScheme
    val progress = state.navigationProgress
    val instruction = when {
        progress == null -> stringResource(Res.string.map_route_loading)
        progress.isArrived -> stringResource(Res.string.map_you_have_arrived)
        else -> routeStepInstruction(progress.currentStep)
    }
    val distance = progress
        ?.takeIf { !it.isArrived }
        ?.let { GeoDistance.formatDistance(it.distanceToManeuverMeters, state.distanceUnit) }

    Row(
        modifier = modifier
            .fillMaxWidth()
            .padding(start = 16.dp, end = 16.dp, top = 12.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(12.dp),
    ) {
        MapCircleButton(
            contentDescription = stringResource(Res.string.map_back),
            onClick = onStop,
            size = 52.dp,
        ) { tint ->
            BackArrowIcon(tint = tint, modifier = Modifier.size(26.dp))
        }

        Row(
            modifier = Modifier
                .weight(1f)
                .shadow(
                    elevation = 8.dp,
                    shape = RoundedCornerShape(26.dp),
                    ambientColor = Color.Black.copy(alpha = 0.30f),
                    spotColor = Color.Black.copy(alpha = 0.22f),
                )
                .clip(RoundedCornerShape(26.dp))
                .background(colors.secondary)
                .padding(horizontal = 18.dp, vertical = 12.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(12.dp),
        ) {
            if (progress != null && !progress.isArrived) {
                Text(
                    text = routeStepGlyph(progress.currentStep),
                    color = colors.onSecondary,
                    fontSize = 22.sp,
                )
            }
            Column(modifier = Modifier.weight(1f)) {
                if (distance != null) {
                    Text(
                        text = distance,
                        color = colors.onSecondary,
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Bold,
                    )
                }
                Text(
                    text = instruction,
                    color = colors.onSecondary,
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Medium,
                    maxLines = 2,
                    overflow = TextOverflow.Ellipsis,
                )
            }
        }
    }
}

@Composable
private fun NavigationSheet(
    state: MapUiState,
    onStop: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val colors = MaterialTheme.colorScheme
    val sheetShape = RoundedCornerShape(topStart = 28.dp, topEnd = 28.dp)
    val progress = state.navigationProgress
    val duration = RouteFormatter.formatDuration(
        seconds = progress?.remainingDurationSeconds ?: state.routePlan?.durationSeconds ?: 0.0,
        secondsSuffix = stringResource(Res.string.map_duration_seconds),
        minutesSuffix = stringResource(Res.string.map_duration_minutes),
        hoursSuffix = stringResource(Res.string.map_duration_hours),
    )
    val remainingDistance = progress?.remainingDistanceMeters ?: state.routePlan?.distanceMeters ?: 0.0

    Column(
        modifier = modifier
            .fillMaxWidth()
            .shadow(
                elevation = 16.dp,
                shape = sheetShape,
                ambientColor = Color.Black.copy(alpha = 0.30f),
                spotColor = Color.Black.copy(alpha = 0.22f),
            )
            .clip(sheetShape)
            .background(colors.surface)
            .spotterNavigationBarsPadding()
            .padding(start = 20.dp, end = 20.dp, top = 14.dp, bottom = 18.dp),
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

        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(12.dp),
        ) {
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = duration,
                    color = colors.onSurface,
                    fontSize = 28.sp,
                    fontWeight = FontWeight.Bold,
                )
                Text(
                    text = GeoDistance.formatDistance(remainingDistance, state.distanceUnit),
                    color = colors.onSurfaceVariant,
                    fontSize = 16.sp,
                )
            }

            Button(
                onClick = onStop,
                modifier = Modifier.height(50.dp),
                shape = RoundedCornerShape(26.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = colors.surfaceVariant,
                    contentColor = colors.onSurface,
                ),
                elevation = ButtonDefaults.buttonElevation(defaultElevation = 0.dp),
            ) {
                Text(
                    text = stringResource(Res.string.map_stop_navigation),
                    fontWeight = FontWeight.Bold,
                    fontSize = 15.sp,
                )
            }
        }
    }
}
