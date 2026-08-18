package com.example.spotter.core.spotui.component

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.luminance
import androidx.compose.ui.unit.dp

@Composable
fun CarbonFiberBackground(
    modifier: Modifier = Modifier,
    content: @Composable () -> Unit,
) {
    val colors = MaterialTheme.colorScheme
    val isDark = colors.background.luminance() < 0.5f

    Box(modifier = modifier) {
        if (isDark) {
            Canvas(modifier = Modifier.fillMaxSize()) {
                val step = 7.dp.toPx()
                drawRect(Color(0xFF0B0B0B))

                var x = -size.height
                while (x < size.width + size.height) {
                    drawLine(
                        color = Color(0xFF1A1A1A),
                        start = Offset(x, 0f),
                        end = Offset(x + size.height, size.height),
                        strokeWidth = 3.2f,
                    )
                    x += step
                }

                var y = -size.width
                while (y < size.height + size.width) {
                    drawLine(
                        color = Color(0xFF121212),
                        start = Offset(0f, y),
                        end = Offset(size.width, y + size.width),
                        strokeWidth = 2.4f,
                    )
                    y += step
                }
            }
        }

        content()
    }
}
