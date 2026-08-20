package com.example.spotter.core.spotui.component

import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.spring
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.StrokeJoin
import androidx.compose.ui.graphics.drawscope.Fill
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.graphics.luminance
import androidx.compose.ui.input.nestedscroll.NestedScrollConnection
import androidx.compose.ui.input.nestedscroll.NestedScrollSource
import androidx.compose.ui.layout.onSizeChanged
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.Velocity
import androidx.compose.ui.unit.dp
import com.example.spotter.core.designsystem.component.spotterNavigationBarsPadding
import com.example.spotter.core.spotui.SpotterTab
import kotlin.math.PI
import kotlin.math.abs
import kotlin.math.cos
import kotlin.math.roundToInt
import kotlin.math.sin
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import androidx.compose.ui.graphics.drawscope.DrawScope

private data class BottomNavEntry(
    val tab: SpotterTab,
    val icon: NavIcon,
)

private val bottomNavEntries = listOf(
    BottomNavEntry(SpotterTab.Search, NavIcon.Home),
    BottomNavEntry(SpotterTab.Map, NavIcon.Map),
    BottomNavEntry(SpotterTab.Favorites, NavIcon.Favorites),
    BottomNavEntry(SpotterTab.Settings, NavIcon.Settings),
)

private val CapsuleShape = RoundedCornerShape(50)
private val HideSpring = spring<Float>(
    dampingRatio = 0.86f,
    stiffness = Spring.StiffnessMedium,
)
private val IndicatorSpring = spring<Float>(
    dampingRatio = 0.78f,
    stiffness = Spring.StiffnessMediumLow,
)

class ScrollAwareBottomBarState internal constructor(
    private val scope: CoroutineScope,
    private val showDelayMs: Long,
) {
    var visible by mutableStateOf(true)
        private set

    private var showJob: Job? = null

    val nestedScrollConnection = object : NestedScrollConnection {
        override fun onPreScroll(available: Offset, source: NestedScrollSource): Offset {
            if (abs(available.y) <= 1.2f) return Offset.Zero
            if (visible) visible = false
            showJob?.cancel()
            showJob = scope.launch {
                delay(showDelayMs)
                visible = true
            }
            return Offset.Zero
        }

        override suspend fun onPreFling(available: Velocity): Velocity {
            if (abs(available.y) > 80f) {
                visible = false
                showJob?.cancel()
                showJob = scope.launch {
                    delay(showDelayMs)
                    visible = true
                }
            }
            return Velocity.Zero
        }
    }
}

@Composable
fun rememberScrollAwareBottomBarState(
    showDelayMs: Long = 700L,
): ScrollAwareBottomBarState {
    val scope = rememberCoroutineScope()
    return remember(scope, showDelayMs) {
        ScrollAwareBottomBarState(scope = scope, showDelayMs = showDelayMs)
    }
}

@Composable
fun SpotterBottomBar(
    selected: SpotterTab,
    onSelected: (SpotterTab) -> Unit,
    modifier: Modifier = Modifier,
    visible: Boolean = true,
) {
    val colors = MaterialTheme.colorScheme
    val isDark = colors.background.luminance() < 0.5f
    val density = LocalDensity.current
    val selectedIndex = bottomNavEntries.indexOfFirst { it.tab == selected }.coerceAtLeast(0)
    val hiddenFraction = animateFloatAsState(
        targetValue = if (visible) 0f else 1f,
        animationSpec = HideSpring,
        label = "bottomBarHidden",
    )
    var rowWidthPx by remember { mutableFloatStateOf(0f) }
    val itemWidthPx = if (bottomNavEntries.isEmpty()) 0f else rowWidthPx / bottomNavEntries.size
    val indicatorX = animateFloatAsState(
        targetValue = selectedIndex * itemWidthPx,
        animationSpec = IndicatorSpring,
        label = "bottomBarIndicator",
    )
    val pillColor = colors.surface.copy(alpha = if (isDark) 0.82f else 0.94f)
    val highlightColor = colors.onSurface.copy(alpha = if (isDark) 0.12f else 0.08f)

    Box(
        modifier = modifier
            .spotterNavigationBarsPadding()
            .padding(bottom = 12.dp)
            .graphicsLayer {
                translationY = hiddenFraction.value * with(density) { 72.dp.toPx() }
                alpha = 1f - hiddenFraction.value
            },
        contentAlignment = Alignment.Center,
    ) {
        Box(
            modifier = Modifier
                .shadow(elevation = 18.dp, shape = CapsuleShape, clip = false)
                .clip(CapsuleShape)
                .background(pillColor)
                .border(
                    width = 1.dp,
                    color = colors.onSurface.copy(alpha = if (isDark) 0.08f else 0.1f),
                    shape = CapsuleShape,
                )
                .padding(horizontal = 6.dp, vertical = 6.dp),
        ) {
            Box(
                modifier = Modifier.onSizeChanged { size ->
                    val width = size.width.toFloat()
                    if (width != rowWidthPx) rowWidthPx = width
                },
            ) {
                if (itemWidthPx > 0f) {
                    Box(
                        modifier = Modifier
                            .align(Alignment.CenterStart)
                            .offset {
                                IntOffset(
                                    x = (indicatorX.value + (itemWidthPx - with(density) { 44.dp.toPx() }) / 2f)
                                        .roundToInt(),
                                    y = 0,
                                )
                            }
                            .size(44.dp)
                            .clip(CircleShape)
                            .background(highlightColor),
                    )
                }

                Row(
                    horizontalArrangement = Arrangement.spacedBy(2.dp),
                    verticalAlignment = Alignment.CenterVertically,
                ) {
                    bottomNavEntries.forEach { entry ->
                        val selectedItem = entry.tab == selected
                        CapsuleNavItem(
                            icon = entry.icon,
                            selected = selectedItem,
                            onClick = { onSelected(entry.tab) },
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun CapsuleNavItem(
    icon: NavIcon,
    selected: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val colors = MaterialTheme.colorScheme
    val tint by animateColorAsState(
        targetValue = if (selected) {
            colors.onSurface
        } else {
            colors.onSurface.copy(alpha = 0.38f)
        },
        animationSpec = tween(durationMillis = 220),
        label = "navIconTint",
    )

    Box(
        modifier = modifier
            .size(width = 52.dp, height = 44.dp)
            .clip(CircleShape)
            .clickable(
                interactionSource = remember { MutableInteractionSource() },
                indication = null,
                onClick = onClick,
            ),
        contentAlignment = Alignment.Center,
    ) {
        NavIconView(
            icon = icon,
            tint = tint,
            selected = selected,
            cutoutColor = colors.surface,
        )
    }
}

private enum class NavIcon {
    Home,
    Map,
    Favorites,
    Settings,
}

@Composable
private fun NavIconView(
    icon: NavIcon,
    tint: Color,
    selected: Boolean,
    cutoutColor: Color,
    modifier: Modifier = Modifier,
) {
    Canvas(modifier = modifier.size(22.dp)) {
        val stroke = Stroke(
            width = 1.8f,
            cap = StrokeCap.Round,
            join = StrokeJoin.Round,
        )
        when (icon) {
            NavIcon.Home -> drawHomeIcon(tint, selected, stroke, cutoutColor)
            NavIcon.Map -> drawMapIcon(tint, stroke)
            NavIcon.Favorites -> drawFavoritesIcon(tint, selected, stroke)
            NavIcon.Settings -> drawSettingsIcon(tint, stroke)
        }
    }
}

private fun DrawScope.drawHomeIcon(
    tint: Color,
    selected: Boolean,
    stroke: Stroke,
    cutoutColor: Color,
) {
    val w = size.width
    val h = size.height

    if (selected) {
        val body = Path().apply {
            moveTo(w * 0.5f, h * 0.12f)
            lineTo(w * 0.88f, h * 0.42f)
            lineTo(w * 0.88f, h * 0.84f)
            lineTo(w * 0.12f, h * 0.84f)
            lineTo(w * 0.12f, h * 0.42f)
            close()
        }
        drawPath(body, color = tint, style = Fill)
        drawRoundRect(
            color = cutoutColor,
            topLeft = Offset(w * 0.38f, h * 0.52f),
            size = Size(w * 0.24f, h * 0.32f),
            cornerRadius = CornerRadius(1.5f, 1.5f),
            style = Fill,
        )
    } else {
        val roof = Path().apply {
            moveTo(w * 0.5f, h * 0.14f)
            lineTo(w * 0.84f, h * 0.42f)
            lineTo(w * 0.16f, h * 0.42f)
            close()
        }
        drawPath(roof, color = tint, style = stroke)
        drawRoundRect(
            color = tint,
            topLeft = Offset(w * 0.22f, h * 0.4f),
            size = Size(w * 0.56f, h * 0.44f),
            cornerRadius = CornerRadius(2f, 2f),
            style = stroke,
        )
        drawRoundRect(
            color = tint,
            topLeft = Offset(w * 0.4f, h * 0.56f),
            size = Size(w * 0.2f, h * 0.28f),
            cornerRadius = CornerRadius(1.5f, 1.5f),
            style = stroke,
        )
    }
}

private fun DrawScope.drawMapIcon(
    tint: Color,
    stroke: Stroke,
) {
    val w = size.width
    val h = size.height
    val mapOutline = Path().apply {
        moveTo(w * 0.14f, h * 0.3f)
        lineTo(w * 0.36f, h * 0.17f)
        lineTo(w * 0.5f, h * 0.26f)
        lineTo(w * 0.64f, h * 0.17f)
        lineTo(w * 0.86f, h * 0.3f)
        lineTo(w * 0.86f, h * 0.76f)
        lineTo(w * 0.64f, h * 0.86f)
        lineTo(w * 0.5f, h * 0.78f)
        lineTo(w * 0.36f, h * 0.86f)
        lineTo(w * 0.14f, h * 0.76f)
        close()
    }
    drawPath(mapOutline, color = tint, style = stroke)
    drawLine(
        color = tint,
        start = Offset(w * 0.36f, h * 0.17f),
        end = Offset(w * 0.36f, h * 0.86f),
        strokeWidth = 1.5f,
        cap = StrokeCap.Round,
    )
    drawLine(
        color = tint,
        start = Offset(w * 0.64f, h * 0.17f),
        end = Offset(w * 0.64f, h * 0.86f),
        strokeWidth = 1.5f,
        cap = StrokeCap.Round,
    )
}

private fun DrawScope.drawFavoritesIcon(
    tint: Color,
    selected: Boolean,
    stroke: Stroke,
) {
    val w = size.width
    val h = size.height
    val path = Path().apply {
        moveTo(w * 0.5f, h * 0.78f)
        cubicTo(w * 0.18f, h * 0.58f, w * 0.14f, h * 0.28f, w * 0.5f, h * 0.4f)
        cubicTo(w * 0.86f, h * 0.28f, w * 0.82f, h * 0.58f, w * 0.5f, h * 0.78f)
        close()
    }
    drawPath(path, color = tint, style = if (selected) Fill else stroke)
}

private fun DrawScope.drawSettingsIcon(
    tint: Color,
    stroke: Stroke,
) {
    val cx = size.width / 2f
    val cy = size.height / 2f
    val radius = size.minDimension * 0.34f
    drawCircle(color = tint, radius = radius, center = Offset(cx, cy), style = stroke)
    drawCircle(color = tint, radius = size.minDimension * 0.12f, center = Offset(cx, cy), style = Fill)
    repeat(6) { index ->
        val angleRadians = ((index * 60.0) - 90.0) * PI / 180.0
        val inner = radius + 2f
        val outer = radius + size.minDimension * 0.1f
        drawLine(
            color = tint,
            start = Offset(
                cx + inner * cos(angleRadians).toFloat(),
                cy + inner * sin(angleRadians).toFloat(),
            ),
            end = Offset(
                cx + outer * cos(angleRadians).toFloat(),
                cy + outer * sin(angleRadians).toFloat(),
            ),
            strokeWidth = 2.4f,
            cap = StrokeCap.Round,
        )
    }
}
