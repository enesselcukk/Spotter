package com.example.spotter.core.spotui.component

import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
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
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.TransformOrigin
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.graphics.luminance
import androidx.compose.ui.layout.onSizeChanged
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.spotter.core.designsystem.theme.SpotterBlue
import com.example.spotter.core.designsystem.theme.SpotterDarkSurfaceElevated
import com.example.spotter.core.designsystem.theme.SpotterGreen
import com.example.spotter.core.spotui.SpotCategories
import com.example.spotter.core.spotui.generated.resources.Res
import com.example.spotter.core.spotui.generated.resources.spot_add_to_favorite
import com.example.spotter.core.spotui.generated.resources.spot_directions
import com.example.spotter.core.spotui.generated.resources.spot_favorited
import com.example.spotter.core.spotui.spotDisplayName
import com.example.spotter.core.spotui.spotDistanceLabel
import com.example.spotter.core.spotui.spotOpeningHoursLabel
import com.example.spotter.core.spotui.spotSocketLabel
import com.example.spotter.feature.home.domain.model.SpotDto
import kotlin.math.roundToInt
import org.jetbrains.compose.resources.stringResource

@Composable
fun SpotExpandedCardOverlay(
    visible: Boolean,
    sourceBounds: SpotCardBounds?,
    spot: SpotDto?,
    isFavorite: Boolean,
    onNavigate: (Long) -> Unit,
    onFavoriteToggle: (Long) -> Unit,
    onDismiss: () -> Unit,
    onExitAnimationEnd: () -> Unit = {},
    mapContent: @Composable (SpotDto) -> Unit,
    modifier: Modifier = Modifier,
) {
    var anchoredBounds by remember { mutableStateOf<SpotCardBounds?>(null) }
    var anchoredSpot by remember { mutableStateOf<SpotDto?>(null) }

    LaunchedEffect(sourceBounds) {
        if (sourceBounds != null) {
            anchoredBounds = sourceBounds
        }
    }
    LaunchedEffect(spot?.id) {
        if (spot != null) {
            anchoredSpot = spot
        }
    }

    val bounds = anchoredBounds
    val activeSpot = anchoredSpot
    if (bounds == null || activeSpot == null) return

    val progress = remember { Animatable(0f) }
    LaunchedEffect(visible, activeSpot.id) {
        if (visible) {
            progress.snapTo(0f)
            progress.animateTo(
                targetValue = 1f,
                animationSpec = tween(
                    durationMillis = ExpandDurationMs,
                    easing = FastOutSlowInEasing,
                ),
            )
        } else {
            progress.animateTo(
                targetValue = 0f,
                animationSpec = tween(
                    durationMillis = CollapseDurationMs,
                    easing = FastOutSlowInEasing,
                ),
            )
            anchoredSpot = null
            anchoredBounds = null
            onExitAnimationEnd()
        }
    }

    if (!visible && progress.value <= 0f) return

    val morphProgress = progress.value
    val detailsAlpha = ((morphProgress - 0.52f) / 0.48f).coerceIn(0f, 1f)

    BoxWithConstraints(
        modifier = modifier.fillMaxSize(),
    ) {
        val density = LocalDensity.current
        val containerWidthPx = constraints.maxWidth.toFloat()
        val containerHeightPx = constraints.maxHeight.toFloat()
        val targetWidthPx = with(density) { (maxWidth - 48.dp).toPx() }
        var targetHeightPx by remember(activeSpot.id) {
            mutableStateOf(with(density) { ExpandedCardFallbackHeight.toPx() })
        }
        val targetCenterX = containerWidthPx / 2f
        val targetCenterY = containerHeightPx / 2f

        val scaleX = lerp(bounds.width / targetWidthPx, 1f, morphProgress)
        val scaleY = lerp(bounds.height / targetHeightPx, 1f, morphProgress)
        val translationX = lerp(bounds.centerX - targetCenterX, 0f, morphProgress)
        val translationY = lerp(bounds.centerY - targetCenterY, 0f, morphProgress)

        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(Color.Black.copy(alpha = 0.45f * morphProgress))
                .clickable(
                    interactionSource = remember { MutableInteractionSource() },
                    indication = null,
                    onClick = onDismiss,
                ),
        )

        Box(
            modifier = Modifier
                .align(Alignment.Center)
                .width(with(density) { targetWidthPx.toDp() })
                .onSizeChanged { size ->
                    if (size.height > 0) {
                        targetHeightPx = size.height.toFloat()
                    }
                }
                .graphicsLayer {
                    this.scaleX = scaleX
                    this.scaleY = scaleY
                    this.translationX = translationX
                    this.translationY = translationY
                    transformOrigin = TransformOrigin.Center
                }
                .clickable(
                    interactionSource = remember { MutableInteractionSource() },
                    indication = null,
                    onClick = {},
                ),
        ) {
            SpotExpandedCard(
                spot = activeSpot,
                isFavorite = isFavorite,
                onNavigate = { onNavigate(activeSpot.id) },
                onFavoriteToggle = { onFavoriteToggle(activeSpot.id) },
                mapContent = { mapContent(activeSpot) },
                detailsAlpha = detailsAlpha,
                modifier = Modifier.fillMaxWidth(),
            )
        }
    }
}

@Composable
fun SpotExpandedCard(
    spot: SpotDto,
    isFavorite: Boolean,
    onNavigate: () -> Unit,
    onFavoriteToggle: () -> Unit,
    mapContent: @Composable (SpotDto) -> Unit,
    modifier: Modifier = Modifier,
    detailsAlpha: Float = 1f,
) {
    val colors = MaterialTheme.colorScheme
    val isDark = colors.background.luminance() < 0.5f
    val cardShape = RoundedCornerShape(24.dp)
    val surfaceColor = if (isDark) SpotterDarkSurfaceElevated else colors.surface
    val borderColor = if (isDark) Color.White.copy(alpha = 0.08f) else colors.outline.copy(alpha = 0.2f)
    val socket = spotSocketLabel(spot)

    Column(
        modifier = modifier
            .shadow(elevation = 24.dp, shape = cardShape, clip = false)
            .clip(cardShape)
            .background(surfaceColor)
            .border(1.dp, borderColor, cardShape)
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(14.dp),
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            SpotExpandedBrandBadge(spot = spot, isFavorite = isFavorite)
            Spacer(modifier = Modifier.width(12.dp))
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = spotDisplayName(spot),
                    color = colors.onSurface,
                    fontWeight = FontWeight.Bold,
                    fontSize = 17.sp,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                )
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = spotDistanceLabel(spot),
                    color = SpotterBlue,
                    fontWeight = FontWeight.SemiBold,
                    fontSize = 13.sp,
                )
                Text(
                    text = spotOpeningHoursLabel(spot),
                    color = SpotterGreen,
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Medium,
                )
            }
        }

        if (!socket.isNullOrBlank()) {
            Text(
                text = "⚡ $socket",
                color = colors.onSurfaceVariant,
                fontSize = 13.sp,
                maxLines = 2,
                overflow = TextOverflow.Ellipsis,
            )
        }

        Column(
            modifier = Modifier.graphicsLayer { alpha = detailsAlpha },
            verticalArrangement = Arrangement.spacedBy(14.dp),
        ) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(148.dp)
                    .clip(RoundedCornerShape(18.dp))
                    .border(1.dp, borderColor, RoundedCornerShape(18.dp)),
            ) {
                mapContent(spot)
            }

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(10.dp),
            ) {
                SpotCardActionButton(
                    label = stringResource(Res.string.spot_directions),
                    icon = SpotCardActionIcon.Navigate,
                    onClick = onNavigate,
                    isDark = isDark,
                    modifier = Modifier.weight(1f),
                )
                SpotCardActionButton(
                    label = if (isFavorite) {
                        stringResource(Res.string.spot_favorited)
                    } else {
                        stringResource(Res.string.spot_add_to_favorite)
                    },
                    icon = SpotCardActionIcon.Favorite,
                    filled = isFavorite,
                    onClick = onFavoriteToggle,
                    isDark = isDark,
                    modifier = Modifier.weight(1f),
                )
            }
        }
    }
}

@Composable
private fun SpotExpandedBrandBadge(
    spot: SpotDto,
    isFavorite: Boolean,
    modifier: Modifier = Modifier,
) {
    val colors = MaterialTheme.colorScheme
    val isDark = colors.background.luminance() < 0.5f

    Box(
        modifier = modifier
            .size(48.dp)
            .clip(CircleShape)
            .background(if (isDark) Color(0xFF111111) else colors.primaryContainer)
            .border(
                width = 1.dp,
                color = if (isDark) Color.White.copy(alpha = 0.08f) else colors.outline.copy(alpha = 0.25f),
                shape = CircleShape,
            ),
        contentAlignment = Alignment.Center,
    ) {
        Text(
            text = SpotCategories.icon(spot.amenity ?: SpotCategories.ALL),
            fontSize = 20.sp,
        )
        if (isFavorite) {
            Box(
                modifier = Modifier
                    .align(Alignment.BottomEnd)
                    .size(12.dp)
                    .clip(CircleShape)
                    .background(Color(0xFFFFC107)),
            )
        }
    }
}

private fun lerp(start: Float, end: Float, fraction: Float): Float =
    start + (end - start) * fraction

private val ExpandedCardFallbackHeight = 380.dp
private const val ExpandDurationMs = 560
private const val CollapseDurationMs = 380
