package com.example.spotter.feature.splash.presentation.ui

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.size
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.StrokeJoin
import androidx.compose.ui.graphics.drawscope.Fill
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.drawscope.scale
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp

@Composable
fun SplashLogo(
    modifier: Modifier = Modifier,
    size: Dp = 168.dp,
) {
    val colors = MaterialTheme.colorScheme
    val cardColor = colors.surfaceVariant
    val strokeColor = colors.onSurface
    val accentColor = colors.primary

    Canvas(modifier = modifier.size(size)) {
        val scaleFactor = this.size.minDimension / 512f
        scale(scaleFactor, scaleFactor, pivot = Offset.Zero) {
            drawRoundRect(
                color = cardColor,
                topLeft = Offset(64f, 32f),
                size = Size(384f, 416f),
                cornerRadius = CornerRadius(32f, 32f),
                style = Fill,
            )

            val pinPath = Path().apply {
                moveTo(256f, 380f)
                lineTo(180f, 270f)
                cubicTo(160f, 240f, 160f, 190f, 200f, 160f)
                cubicTo(230f, 135f, 280f, 135f, 310f, 160f)
                cubicTo(350f, 190f, 350f, 240f, 332f, 270f)
                close()
            }
            drawPath(
                path = pinPath,
                color = strokeColor,
                style = Stroke(
                    width = 20f,
                    cap = StrokeCap.Round,
                    join = StrokeJoin.Round,
                ),
            )

            val signalPath = Path().apply {
                moveTo(220f, 190f)
                cubicTo(240f, 175f, 272f, 175f, 292f, 190f)
            }
            drawPath(
                path = signalPath,
                color = accentColor,
                style = Stroke(
                    width = 14f,
                    cap = StrokeCap.Round,
                ),
            )

            drawCircle(
                color = strokeColor,
                radius = 16f,
                center = Offset(256f, 220f),
                style = Fill,
            )
        }
    }
}
