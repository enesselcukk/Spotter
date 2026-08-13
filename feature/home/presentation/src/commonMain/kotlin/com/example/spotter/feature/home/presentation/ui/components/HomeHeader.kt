package com.example.spotter.feature.home.presentation.ui.components

import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.drawscope.rotate
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.spotter.core.designsystem.theme.SpotterBlue
import com.example.spotter.core.designsystem.theme.SpotterYellow
import com.example.spotter.feature.home.presentation.generated.resources.Res
import com.example.spotter.feature.home.presentation.generated.resources.home_title
import com.example.spotter.feature.home.presentation.ui.resolveLocationLabel
import org.jetbrains.compose.resources.stringResource

@Composable
fun HomeBrandingHeader(
    locationLabel: String?,
    usesDeviceLocation: Boolean,
    modifier: Modifier = Modifier,
) {
    val colors = MaterialTheme.colorScheme
    val resolvedLabel = resolveLocationLabel(
        locationLabel = locationLabel,
        usesDeviceLocation = usesDeviceLocation,
    )

    Column(
        modifier = modifier
            .fillMaxWidth()
            .padding(top = 8.dp, bottom = 4.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        HomeHeaderLogo(modifier = Modifier.size(72.dp))
        Spacer(modifier = Modifier.height(12.dp))
        Text(
            text = stringResource(Res.string.home_title),
            color = colors.onBackground,
            fontWeight = FontWeight.Bold,
            fontSize = 26.sp,
            textAlign = TextAlign.Center,
        )
        Spacer(modifier = Modifier.height(8.dp))
        Row(verticalAlignment = Alignment.CenterVertically) {
            Text(text = "📍", fontSize = 14.sp)
            Spacer(modifier = Modifier.width(6.dp))
            Text(
                text = resolvedLabel,
                color = SpotterBlue,
                fontSize = 14.sp,
                fontWeight = FontWeight.Medium,
            )
        }
    }
}

@Composable
private fun HomeHeaderLogo(modifier: Modifier = Modifier) {
    val infiniteTransition = rememberInfiniteTransition(label = "header_orbit")
    val rotation by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = 360f,
        animationSpec = infiniteRepeatable(
            animation = tween(durationMillis = 8000, easing = LinearEasing),
            repeatMode = RepeatMode.Restart,
        ),
        label = "header_rotation",
    )

    Canvas(modifier = modifier) {
        val center = Offset(size.width / 2f, size.height / 2f)
        val radius = size.minDimension * 0.38f

        rotate(rotation, center) {
            drawArc(
                color = SpotterBlue.copy(alpha = 0.85f),
                startAngle = 30f,
                sweepAngle = 280f,
                useCenter = false,
                topLeft = Offset(center.x - radius, center.y - radius),
                size = androidx.compose.ui.geometry.Size(radius * 2, radius * 2),
                style = Stroke(width = 4f, cap = StrokeCap.Round),
            )
        }

        drawArc(
            color = SpotterYellow.copy(alpha = 0.9f),
            startAngle = 160f,
            sweepAngle = 220f,
            useCenter = false,
            topLeft = Offset(center.x - radius * 0.78f, center.y - radius * 0.78f),
            size = androidx.compose.ui.geometry.Size(radius * 1.56f, radius * 1.56f),
            style = Stroke(width = 3f, cap = StrokeCap.Round),
        )

        drawCircle(
            brush = Brush.radialGradient(
                colors = listOf(SpotterBlue.copy(alpha = 0.35f), Color.Transparent),
                center = center,
                radius = size.minDimension * 0.22f,
            ),
            radius = size.minDimension * 0.22f,
            center = center,
        )
    }
}
