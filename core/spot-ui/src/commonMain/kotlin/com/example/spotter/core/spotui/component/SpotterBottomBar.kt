package com.example.spotter.core.spotui.component

import androidx.compose.animation.animateColorAsState
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.StrokeJoin
import androidx.compose.ui.graphics.drawscope.Fill
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.luminance
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.spotter.core.designsystem.component.spotterNavigationBarsPadding
import com.example.spotter.core.designsystem.theme.SpotterBlue
import com.example.spotter.core.spotui.SpotterTab
import com.example.spotter.core.spotui.generated.resources.Res
import com.example.spotter.core.spotui.generated.resources.spot_tab_favorites
import com.example.spotter.core.spotui.generated.resources.spot_tab_map
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
    BottomNavEntry(SpotterTab.Search, Res.string.spot_tab_search, NavIcon.Home),
    BottomNavEntry(SpotterTab.Map, Res.string.spot_tab_map, NavIcon.Map),
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

    Column(
        modifier = modifier
            .fillMaxWidth()
            .background(if (isDark) Color(0xFF0F0F0F) else colors.surface)
            .spotterNavigationBarsPadding(),
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(1.dp)
                .background(
                    if (isDark) Color.White.copy(alpha = 0.07f) else colors.outline.copy(alpha = 0.25f),
                ),
        )
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .height(68.dp)
                .padding(horizontal = 4.dp),
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
    val activeColor = if (isDark) SpotterBlue else colors.primary
    val inactiveColor = if (isDark) Color(0xFF8A8A8A) else colors.onSurfaceVariant.copy(alpha = 0.72f)

    val tint by animateColorAsState(
        targetValue = if (selected) activeColor else inactiveColor,
        label = "nav_icon_color",
    )

    Column(
        modifier = modifier
            .clickable(
                interactionSource = remember { MutableInteractionSource() },
                indication = null,
                onClick = onClick,
            )
            .padding(vertical = 8.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(4.dp),
    ) {
        NavIconView(
            icon = icon,
            tint = tint,
            selected = selected,
        )
        Text(
            text = label,
            color = tint,
            fontSize = 11.sp,
            fontWeight = if (selected) FontWeight.SemiBold else FontWeight.Normal,
            maxLines = 1,
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
    modifier: Modifier = Modifier,
) {
    Canvas(modifier = modifier.size(24.dp)) {
        val stroke = Stroke(
            width = 1.75f,
            cap = StrokeCap.Round,
            join = StrokeJoin.Round,
        )
        when (icon) {
            NavIcon.Home -> drawHomeIcon(tint, selected, stroke)
            NavIcon.Map -> drawMapIcon(tint, stroke)
            NavIcon.Favorites -> drawFavoritesIcon(tint, selected, stroke)
            NavIcon.Settings -> drawSettingsIcon(tint, stroke)
        }
    }
}

private fun androidx.compose.ui.graphics.drawscope.DrawScope.drawHomeIcon(
    tint: Color,
    selected: Boolean,
    stroke: Stroke,
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
            color = Color(0xFF0F0F0F),
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

private fun androidx.compose.ui.graphics.drawscope.DrawScope.drawMapIcon(
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

private fun androidx.compose.ui.graphics.drawscope.DrawScope.drawFavoritesIcon(
    tint: Color,
    selected: Boolean,
    stroke: Stroke,
) {
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
    if (selected) {
        drawPath(path, color = tint, style = Fill)
    } else {
        drawPath(path, color = tint, style = stroke)
    }
}

private fun androidx.compose.ui.graphics.drawscope.DrawScope.drawSettingsIcon(
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
