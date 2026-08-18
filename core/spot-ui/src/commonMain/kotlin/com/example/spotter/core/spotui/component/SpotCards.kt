package com.example.spotter.core.spotui.component

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Fill
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.luminance
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.spotter.core.designsystem.theme.SpotterDarkCardButton
import com.example.spotter.core.designsystem.theme.SpotterDarkSurfaceElevated
import com.example.spotter.core.designsystem.theme.SpotterGreen
import com.example.spotter.core.designsystem.theme.SpotterYellow
import com.example.spotter.core.spotui.SpotCategories
import com.example.spotter.core.spotui.generated.resources.Res
import com.example.spotter.core.spotui.generated.resources.spot_add_to_favorite
import com.example.spotter.core.spotui.generated.resources.spot_directions
import com.example.spotter.core.spotui.generated.resources.spot_favorited
import com.example.spotter.core.spotui.spotCategoryLabel
import com.example.spotter.core.spotui.spotDisplayName
import com.example.spotter.core.spotui.spotDistanceLabel
import com.example.spotter.core.spotui.spotOpeningHoursLabel
import com.example.spotter.core.spotui.spotSocketLabel
import com.example.spotter.feature.home.domain.model.SpotDto
import org.jetbrains.compose.resources.stringResource

@Composable
fun SpotDetailCard(
    spot: SpotDto,
    isFavorite: Boolean,
    markerIndex: Int = 1,
    onDirections: (Double, Double, String?) -> Unit,
    onFavoriteToggle: (Long) -> Unit,
    onMapPreviewClick: ((Long) -> Unit)? = null,
    highlighted: Boolean = false,
    modifier: Modifier = Modifier,
) {
    val colors = MaterialTheme.colorScheme
    val isDark = colors.background.luminance() < 0.5f
    val cardShape = RoundedCornerShape(18.dp)
    val borderColor = when {
        highlighted && isDark -> Color.White.copy(alpha = 0.12f)
        isDark -> Color.White.copy(alpha = 0.06f)
        highlighted -> colors.outline.copy(alpha = 0.55f)
        else -> colors.outline.copy(alpha = 0.35f)
    }
    val socket = spotSocketLabel(spot)
    val favoriteLabel = if (isFavorite) {
        stringResource(Res.string.spot_favorited)
    } else {
        stringResource(Res.string.spot_add_to_favorite)
    }

    Column(
        modifier = modifier
            .fillMaxWidth()
            .clip(cardShape)
            .background(if (isDark) SpotterDarkSurfaceElevated else colors.surface)
            .border(1.dp, borderColor, cardShape)
            .padding(14.dp),
    ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            SpotBrandBadge(
                spot = spot,
                isFavorite = isFavorite,
                modifier = Modifier.size(44.dp),
            )
            Spacer(modifier = Modifier.width(12.dp))
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = spotDisplayName(spot),
                    color = colors.onSurface,
                    fontWeight = FontWeight.SemiBold,
                    fontSize = 16.sp,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                )
                if (!socket.isNullOrBlank()) {
                    Spacer(modifier = Modifier.height(3.dp))
                    Text(
                        text = "⚡ $socket",
                        color = colors.onSurfaceVariant,
                        fontSize = 12.sp,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis,
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(12.dp))

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(12.dp),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            SpotMapPreview(
                markerIndex = markerIndex,
                onClick = onMapPreviewClick?.let { callback -> { callback(spot.id) } },
                modifier = Modifier
                    .weight(0.42f)
                    .height(86.dp),
            )
            Column(
                modifier = Modifier.weight(0.58f),
                verticalArrangement = Arrangement.spacedBy(6.dp),
            ) {
                Text(
                    text = spotDistanceLabel(spot),
                    color = colors.onSurface,
                    fontWeight = FontWeight.Medium,
                    fontSize = 14.sp,
                )
                Text(
                    text = spotOpeningHoursLabel(spot),
                    color = SpotterGreen,
                    fontSize = 13.sp,
                    fontWeight = FontWeight.Medium,
                )
                if (!socket.isNullOrBlank()) {
                    Text(
                        text = socket,
                        color = colors.onSurfaceVariant,
                        fontSize = 12.sp,
                        maxLines = 2,
                        overflow = TextOverflow.Ellipsis,
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(12.dp))

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(10.dp),
        ) {
            SpotCardActionButton(
                label = stringResource(Res.string.spot_directions),
                icon = SpotCardActionIcon.Navigate,
                onClick = { onDirections(spot.lat, spot.lon, spot.name) },
                isDark = isDark,
                modifier = Modifier.weight(1f),
            )
            SpotCardActionButton(
                label = favoriteLabel,
                icon = SpotCardActionIcon.Favorite,
                filled = isFavorite,
                onClick = { onFavoriteToggle(spot.id) },
                isDark = isDark,
                modifier = Modifier.weight(1f),
            )
        }
    }
}

@Composable
fun SpotCompactItem(
    spot: SpotDto,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val colors = MaterialTheme.colorScheme
    val isDark = colors.background.luminance() < 0.5f

    Row(
        modifier = modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(16.dp))
            .background(if (isDark) colors.surface.copy(alpha = 0.55f) else colors.surface)
            .border(
                width = 1.dp,
                color = colors.outline.copy(alpha = if (isDark) 0.2f else 0.35f),
                shape = RoundedCornerShape(16.dp),
            )
            .clickable(onClick = onClick)
            .padding(horizontal = 14.dp, vertical = 12.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        SpotBrandBadge(spot = spot, modifier = Modifier.size(40.dp))
        Spacer(modifier = Modifier.width(12.dp))
        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = spotDisplayName(spot),
                color = colors.onSurface,
                fontWeight = FontWeight.SemiBold,
                fontSize = 15.sp,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
            )
            Text(
                text = spotSocketLabel(spot) ?: spotCategoryLabel(spot.amenity ?: SpotCategories.ALL),
                color = colors.onSurfaceVariant,
                fontSize = 12.sp,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
            )
        }
    }
}

@Composable
private fun SpotCardActionButton(
    label: String,
    icon: SpotCardActionIcon,
    onClick: () -> Unit,
    isDark: Boolean,
    modifier: Modifier = Modifier,
    filled: Boolean = false,
) {
    val colors = MaterialTheme.colorScheme
    val shape = RoundedCornerShape(14.dp)
    val background = if (isDark) SpotterDarkCardButton else colors.surfaceVariant
    val content = if (isDark) Color.White else colors.onSurface

    Row(
        modifier = modifier
            .height(44.dp)
            .clip(shape)
            .background(background)
            .border(
                width = 1.dp,
                color = if (isDark) Color.White.copy(alpha = 0.06f) else colors.outline.copy(alpha = 0.35f),
                shape = shape,
            )
            .clickable(onClick = onClick),
        horizontalArrangement = Arrangement.Center,
        verticalAlignment = Alignment.CenterVertically,
    ) {
        SpotCardActionIconView(icon = icon, tint = content, filled = filled)
        Spacer(modifier = Modifier.width(8.dp))
        Text(
            text = label,
            color = content,
            fontWeight = FontWeight.Medium,
            fontSize = 13.sp,
        )
    }
}

private enum class SpotCardActionIcon {
    Navigate,
    Favorite,
}

@Composable
private fun SpotCardActionIconView(
    icon: SpotCardActionIcon,
    tint: Color,
    modifier: Modifier = Modifier,
    filled: Boolean = false,
) {
    Canvas(modifier = modifier.size(16.dp)) {
        val stroke = Stroke(width = 1.8f, cap = StrokeCap.Round)
        when (icon) {
            SpotCardActionIcon.Navigate -> {
                val path = Path().apply {
                    moveTo(size.width * 0.18f, size.height * 0.82f)
                    lineTo(size.width * 0.18f, size.height * 0.18f)
                    lineTo(size.width * 0.86f, size.height * 0.5f)
                    close()
                }
                drawPath(path, color = tint, style = Fill)
            }

            SpotCardActionIcon.Favorite -> {
                val w = size.width
                val h = size.height
                val path = Path().apply {
                    moveTo(w * 0.5f, h * 0.78f)
                    cubicTo(
                        w * 0.18f, h * 0.58f,
                        w * 0.14f, h * 0.28f,
                        w * 0.5f, h * 0.4f,
                    )
                    cubicTo(
                        w * 0.86f, h * 0.28f,
                        w * 0.82f, h * 0.58f,
                        w * 0.5f, h * 0.78f,
                    )
                    close()
                }
                if (filled) {
                    drawPath(path, color = tint, style = Fill)
                } else {
                    drawPath(path, color = tint, style = stroke)
                }
            }
        }
    }
}

@Composable
private fun SpotBrandBadge(
    spot: SpotDto,
    modifier: Modifier = Modifier,
    isFavorite: Boolean = false,
) {
    val colors = MaterialTheme.colorScheme
    val isDark = colors.background.luminance() < 0.5f

    Box(
        modifier = modifier
            .clip(CircleShape)
            .background(if (isDark) Color(0xFF111111) else colors.primaryContainer)
            .border(
                width = 1.dp,
                color = if (isDark) Color.White.copy(alpha = 0.08f) else colors.outline.copy(alpha = 0.3f),
                shape = CircleShape,
            ),
        contentAlignment = Alignment.Center,
    ) {
        Text(
            text = SpotCategories.icon(spot.amenity ?: SpotCategories.ALL),
            fontSize = 18.sp,
        )
        if (isFavorite) {
            Box(
                modifier = Modifier
                    .align(Alignment.BottomEnd)
                    .size(12.dp)
                    .clip(CircleShape)
                    .background(SpotterYellow),
            )
        }
    }
}

@Composable
private fun SpotMapPreview(
    markerIndex: Int,
    onClick: (() -> Unit)? = null,
    modifier: Modifier = Modifier,
) {
    val land = Color(0xFF1A2228)
    val road = Color(0xFF3A4650)
    val water = Color(0xFF24343C)

    Box(
        modifier = modifier
            .clip(RoundedCornerShape(14.dp))
            .background(land)
            .then(
                if (onClick != null) {
                    Modifier.clickable(onClick = onClick)
                } else {
                    Modifier
                },
            ),
    ) {
        Canvas(modifier = Modifier.matchParentSize()) {
            drawRect(water, topLeft = Offset(size.width * 0.62f, size.height * 0.55f), size = size / 2f)
            drawLine(
                color = road,
                start = Offset(0f, size.height * 0.32f),
                end = Offset(size.width, size.height * 0.28f),
                strokeWidth = 5f,
            )
            drawLine(
                color = road,
                start = Offset(size.width * 0.28f, 0f),
                end = Offset(size.width * 0.42f, size.height),
                strokeWidth = 4f,
            )
            drawLine(
                color = road,
                start = Offset(0f, size.height * 0.72f),
                end = Offset(size.width, size.height * 0.58f),
                strokeWidth = 3.5f,
            )
        }
        Box(
            modifier = Modifier
                .align(Alignment.Center)
                .size(26.dp)
                .clip(CircleShape)
                .background(SpotterYellow),
            contentAlignment = Alignment.Center,
        ) {
            Text(
                text = markerIndex.toString(),
                color = Color.Black,
                fontWeight = FontWeight.Bold,
                fontSize = 12.sp,
            )
        }
    }
}
