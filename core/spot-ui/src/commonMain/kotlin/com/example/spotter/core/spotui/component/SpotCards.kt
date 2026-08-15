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
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.luminance
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.spotter.core.designsystem.theme.SpotterBlue
import com.example.spotter.core.designsystem.theme.SpotterDarkSurfaceElevated
import com.example.spotter.core.designsystem.theme.SpotterGreen
import com.example.spotter.core.designsystem.theme.SpotterYellow
import com.example.spotter.core.spotui.SpotCategories
import com.example.spotter.core.spotui.generated.resources.Res
import com.example.spotter.core.spotui.generated.resources.spot_directions
import com.example.spotter.core.spotui.generated.resources.spot_favorite
import com.example.spotter.core.spotui.generated.resources.spot_favorited
import com.example.spotter.core.spotui.spotCategoryLabel
import com.example.spotter.core.spotui.spotDisplayName
import com.example.spotter.core.spotui.spotDistanceLabel
import com.example.spotter.core.spotui.spotOpeningHoursLabel
import com.example.spotter.core.spotui.spotOperatorLabel
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
    val cardShape = RoundedCornerShape(20.dp)
    val borderColor = when {
        !highlighted -> colors.outline.copy(alpha = if (isDark) 0.25f else 0.4f)
        isDark -> SpotterBlue.copy(alpha = 0.45f)
        else -> colors.secondary.copy(alpha = 0.5f)
    }

    Card(
        modifier = modifier
            .fillMaxWidth()
            .border(
                width = if (highlighted) 1.5.dp else 1.dp,
                color = borderColor,
                shape = cardShape,
            ),
        shape = cardShape,
        colors = CardDefaults.cardColors(
            containerColor = if (isDark) SpotterDarkSurfaceElevated else colors.surface,
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = 0.dp),
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                SpotBrandBadge(
                    spot = spot,
                    modifier = Modifier.size(48.dp),
                )
                Spacer(modifier = Modifier.width(12.dp))
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = spotDisplayName(spot),
                        color = colors.onSurface,
                        fontWeight = FontWeight.Bold,
                        fontSize = 16.sp,
                        maxLines = 2,
                        overflow = TextOverflow.Ellipsis,
                    )
                    Spacer(modifier = Modifier.height(6.dp))
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Text(
                            text = spotDistanceLabel(spot),
                            color = colors.onSurfaceVariant,
                            fontSize = 13.sp,
                        )
                        Text(
                            text = "  •  ",
                            color = colors.onSurfaceVariant,
                        )
                        Text(
                            text = spotOpeningHoursLabel(spot),
                            color = SpotterGreen,
                            fontSize = 13.sp,
                            fontWeight = FontWeight.Medium,
                        )
                    }
                }
            }

            if (highlighted) {
                Spacer(modifier = Modifier.height(14.dp))
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
                            .height(78.dp),
                    )
                    Column(modifier = Modifier.weight(0.58f)) {
                        spotSocketLabel(spot)?.let { socket ->
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Text(text = "🔌", fontSize = 14.sp)
                                Spacer(modifier = Modifier.width(6.dp))
                                Text(
                                    text = socket,
                                    color = SpotterBlue,
                                    fontSize = 13.sp,
                                    fontWeight = FontWeight.Medium,
                                    maxLines = 2,
                                    overflow = TextOverflow.Ellipsis,
                                )
                            }
                        }
                        spotOperatorLabel(spot)?.let { operator ->
                            Spacer(modifier = Modifier.height(6.dp))
                            Text(
                                text = operator,
                                color = colors.onSurfaceVariant,
                                fontSize = 12.sp,
                                maxLines = 1,
                                overflow = TextOverflow.Ellipsis,
                            )
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(14.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(10.dp),
            ) {
                Button(
                    onClick = { onDirections(spot.lat, spot.lon, spot.name) },
                    modifier = Modifier.weight(1f),
                    shape = RoundedCornerShape(24.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = SpotterBlue,
                        contentColor = Color.White,
                    ),
                ) {
                    Text(
                        text = "➤ ${stringResource(Res.string.spot_directions)}",
                        fontWeight = FontWeight.SemiBold,
                        fontSize = 14.sp,
                    )
                }
                Button(
                    onClick = { onFavoriteToggle(spot.id) },
                    modifier = Modifier.weight(1f),
                    shape = RoundedCornerShape(24.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = if (isDark) colors.background else colors.surfaceVariant,
                        contentColor = colors.onSurface,
                    ),
                ) {
                    Text(
                        text = if (isFavorite) {
                            "${stringResource(Res.string.spot_favorited)} ★"
                        } else {
                            "${stringResource(Res.string.spot_favorite)} ☆"
                        },
                        fontWeight = FontWeight.Medium,
                        fontSize = 14.sp,
                        color = if (isFavorite) SpotterYellow else colors.onSurface,
                    )
                }
            }
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
private fun SpotBrandBadge(
    spot: SpotDto,
    modifier: Modifier = Modifier,
) {
    val colors = MaterialTheme.colorScheme
    val isDark = colors.background.luminance() < 0.5f
    val initials = spotDisplayName(spot)
        .split(" ")
        .take(2)
        .mapNotNull { it.firstOrNull()?.uppercaseChar()?.toString() }
        .joinToString("")
        .ifBlank { SpotCategories.icon(spot.amenity ?: SpotCategories.ALL) }

    Box(
        modifier = modifier
            .clip(CircleShape)
            .background(if (isDark) colors.background else colors.primaryContainer),
        contentAlignment = Alignment.Center,
    ) {
        Text(
            text = initials.take(2),
            color = if (isDark) SpotterYellow else colors.onPrimaryContainer,
            fontWeight = FontWeight.Bold,
            fontSize = 14.sp,
        )
    }
}

@Composable
private fun SpotMapPreview(
    markerIndex: Int,
    onClick: (() -> Unit)? = null,
    modifier: Modifier = Modifier,
) {
    val gridColor = Color(0xFF2E3A4A)
    val lineColor = Color(0xFF3D4F63)

    Box(
        modifier = modifier
            .clip(RoundedCornerShape(14.dp))
            .background(gridColor)
            .then(
                if (onClick != null) {
                    Modifier.clickable(onClick = onClick)
                } else {
                    Modifier
                },
            ),
    ) {
        Canvas(modifier = Modifier.matchParentSize()) {
            val step = size.width / 5f
            var x = step
            while (x < size.width) {
                drawLine(lineColor, Offset(x, 0f), Offset(x, size.height), strokeWidth = 1f)
                x += step
            }
            var y = size.height / 4f
            while (y < size.height) {
                drawLine(lineColor, Offset(0f, y), Offset(size.width, y), strokeWidth = 1f)
                y += size.height / 4f
            }
        }
        Box(
            modifier = Modifier
                .align(Alignment.Center)
                .size(28.dp)
                .clip(CircleShape)
                .background(SpotterYellow),
            contentAlignment = Alignment.Center,
        ) {
            Text(
                text = markerIndex.toString(),
                color = Color.Black,
                fontWeight = FontWeight.Bold,
                fontSize = 13.sp,
            )
        }
    }
}
