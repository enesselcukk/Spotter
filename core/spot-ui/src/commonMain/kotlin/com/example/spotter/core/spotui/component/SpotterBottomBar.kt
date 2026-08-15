package com.example.spotter.core.spotui.component

import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.spring
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.luminance
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.spotter.core.designsystem.component.spotterNavigationBarsPadding
import com.example.spotter.core.designsystem.theme.SpotterBlue
import com.example.spotter.core.designsystem.theme.SpotterYellow
import com.example.spotter.core.spotui.SpotterTab
import com.example.spotter.core.spotui.generated.resources.Res
import com.example.spotter.core.spotui.generated.resources.spot_tab_favorites
import com.example.spotter.core.spotui.generated.resources.spot_tab_search
import com.example.spotter.core.spotui.generated.resources.spot_tab_settings
import kotlin.math.PI
import kotlin.math.cos
import kotlin.math.sin
import org.jetbrains.compose.resources.StringResource
import org.jetbrains.compose.resources.stringResource

private data class BottomNavEntry(
    val tab: SpotterTab,
    val labelRes: StringResource,
    val icon: NavIcon,
)

private val bottomNavEntries = listOf(
    BottomNavEntry(SpotterTab.Search, Res.string.spot_tab_search, NavIcon.Search),
    BottomNavEntry(SpotterTab.Favorites, Res.string.spot_tab_favorites, NavIcon.Favorites),
    BottomNavEntry(SpotterTab.Settings, Res.string.spot_tab_settings, NavIcon.Settings),
)

@Composable
fun SpotterBottomBar(
    selected: SpotterTab,
    onSelected: (SpotterTab) -> Unit,
    modifier: Modifier = Modifier,
) {
    val colors = MaterialTheme.colorScheme
    val isDark = colors.background.luminance() < 0.5f
    val barShape = RoundedCornerShape(32.dp)
    val selectedIndex = bottomNavEntries.indexOfFirst { it.tab == selected }.coerceAtLeast(0)

    Box(
        modifier = modifier
            .fillMaxWidth()
            .spotterNavigationBarsPadding()
            .padding(start = 20.dp, end = 20.dp, top = 8.dp, bottom = 12.dp),
    ) {
        BoxWithConstraints(
            modifier = Modifier
                .fillMaxWidth()
                .shadow(
                    elevation = if (isDark) 16.dp else 10.dp,
                    shape = barShape,
                    ambientColor = Color.Black.copy(alpha = 0.35f),
                    spotColor = Color.Black.copy(alpha = 0.25f),
                )
                .clip(barShape)
                .background(
                    if (isDark) {
                        colors.surface.copy(alpha = 0.94f)
                    } else {
                        colors.surface
                    },
                )
                .border(
                    width = 1.dp,
                    color = if (isDark) {
                        Color.White.copy(alpha = 0.08f)
                    } else {
                        colors.outline.copy(alpha = 0.25f)
                    },
                    shape = barShape,
                )
                .padding(horizontal = 6.dp, vertical = 8.dp),
        ) {
            val itemWidth = maxWidth / bottomNavEntries.size
            val indicatorOffset by animateFloatAsState(
                targetValue = selectedIndex * itemWidth.value,
                animationSpec = spring(stiffness = Spring.StiffnessMediumLow, dampingRatio = 0.78f),
                label = "nav_indicator_offset",
            )

            Box(
                modifier = Modifier
                    .offset(x = indicatorOffset.dp)
                    .width(itemWidth)
                    .height(52.dp)
                    .padding(horizontal = 4.dp)
                    .clip(RoundedCornerShape(22.dp))
                    .background(
                        if (isDark) {
                            SpotterBlue.copy(alpha = 0.16f)
                        } else {
                            colors.primaryContainer.copy(alpha = 0.55f)
                        },
                    ),
            )

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceEvenly,
                verticalAlignment = Alignment.CenterVertically,
            ) {
                bottomNavEntries.forEach { entry ->
                    BottomBarItem(
                        label = stringResource(entry.labelRes),
                        icon = entry.icon,
                        selected = entry.tab == selected,
                        onClick = { onSelected(entry.tab) },
                        isDark = isDark,
                        modifier = Modifier.weight(1f),
                    )
                }
            }
        }
    }
}

@Composable
private fun BottomBarItem(
    label: String,
    icon: NavIcon,
    selected: Boolean,
    onClick: () -> Unit,
    isDark: Boolean,
    modifier: Modifier = Modifier,
) {
    val colors = MaterialTheme.colorScheme
    val labelColor by animateColorAsState(
        targetValue = when {
            selected && isDark -> SpotterBlue
            selected -> colors.primary
            else -> colors.onSurfaceVariant.copy(alpha = 0.72f)
        },
        label = "nav_label_color",
    )
    val iconTint by animateColorAsState(
        targetValue = when {
            selected && isDark -> SpotterBlue
            selected -> colors.primary
            else -> colors.onSurfaceVariant.copy(alpha = 0.65f)
        },
        label = "nav_icon_color",
    )

    Column(
        modifier = modifier
            .clip(RoundedCornerShape(20.dp))
            .clickable(
                interactionSource = remember { MutableInteractionSource() },
                indication = null,
                onClick = onClick,
            )
            .padding(vertical = 6.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        NavIconView(
            icon = icon,
            tint = iconTint,
            filled = selected && icon == NavIcon.Favorites,
        )
        Text(
            text = label,
            color = labelColor,
            fontSize = 11.sp,
            fontWeight = if (selected) FontWeight.SemiBold else FontWeight.Normal,
            modifier = Modifier.padding(top = 4.dp),
        )
    }
}

private enum class NavIcon {
    Search,
    Favorites,
    Settings,
}

@Composable
private fun NavIconView(
    icon: NavIcon,
    tint: Color,
    filled: Boolean,
    modifier: Modifier = Modifier,
) {
    Canvas(modifier = modifier.size(24.dp)) {
        val stroke = Stroke(width = 2f, cap = StrokeCap.Round)
        when (icon) {
            NavIcon.Search -> {
                val radius = size.minDimension * 0.28f
                val center = Offset(size.width * 0.42f, size.height * 0.42f)
                drawCircle(color = tint, radius = radius, center = center, style = stroke)
                drawLine(
                    color = tint,
                    start = Offset(center.x + radius * 0.65f, center.y + radius * 0.65f),
                    end = Offset(size.width * 0.82f, size.height * 0.82f),
                    strokeWidth = 2.2f,
                    cap = StrokeCap.Round,
                )
            }

            NavIcon.Favorites -> {
                val path = Path().apply {
                    moveTo(size.width * 0.5f, size.height * 0.82f)
                    cubicTo(
                        size.width * 0.12f, size.height * 0.58f,
                        size.width * 0.12f, size.height * 0.22f,
                        size.width * 0.5f, size.height * 0.38f,
                    )
                    cubicTo(
                        size.width * 0.88f, size.height * 0.22f,
                        size.width * 0.88f, size.height * 0.58f,
                        size.width * 0.5f, size.height * 0.82f,
                    )
                    close()
                }
                if (filled) {
                    drawPath(path, color = SpotterYellow)
                } else {
                    drawPath(path, color = tint, style = stroke)
                }
            }

            NavIcon.Settings -> {
                val cx = size.width / 2f
                val cy = size.height / 2f
                drawCircle(color = tint, radius = size.minDimension * 0.14f, center = Offset(cx, cy))
                repeat(8) { index ->
                    val angleRadians = ((index * 45.0) - 90.0) * PI / 180.0
                    val inner = size.minDimension * 0.22f
                    val outer = size.minDimension * 0.38f
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
                        strokeWidth = 2.2f,
                        cap = StrokeCap.Round,
                    )
                }
            }
        }
    }
}
