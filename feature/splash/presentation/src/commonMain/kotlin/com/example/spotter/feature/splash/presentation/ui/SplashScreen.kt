package com.example.spotter.feature.splash.presentation.ui

import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.drawscope.rotate
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.spotter.feature.splash.presentation.generated.resources.Res
import com.example.spotter.feature.splash.presentation.generated.resources.splash_initializing
import com.example.spotter.feature.splash.presentation.generated.resources.splash_tagline
import com.example.spotter.feature.splash.presentation.platform.LocationPermissionEffect
import org.jetbrains.compose.resources.stringResource
import org.koin.compose.viewmodel.koinViewModel

private val SplashBackgroundTop = Color(0xFF0A0E14)
private val SplashBackgroundBottom = Color(0xFF141C28)
private val SplashBlue = Color(0xFF4FACFE)
private val SplashGold = Color(0xFFFFC107)
private val SplashSilver = Color(0xFFE8EAED)

@Composable
fun SplashScreen(
    onNavigateHome: () -> Unit,
    viewModel: SplashViewModel = koinViewModel(),
) {
    val progress by viewModel.progress.collectAsStateWithLifecycle()

    LocationPermissionEffect(onPermissionResolved = viewModel::startPreloadIfNeeded)

    LaunchedEffect(viewModel) {
        viewModel.navigateHome.collect {
            onNavigateHome()
        }
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(
                Brush.verticalGradient(
                    colors = listOf(SplashBackgroundTop, SplashBackgroundBottom),
                ),
            ),
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 32.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center,
        ) {
            SplashLogo(modifier = Modifier.size(220.dp))
            Spacer(modifier = Modifier.height(28.dp))
            Text(
                text = "SPOTTER",
                color = SplashSilver,
                fontSize = 42.sp,
                fontWeight = FontWeight.Bold,
                letterSpacing = 6.sp,
            )
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = stringResource(Res.string.splash_tagline),
                color = SplashSilver.copy(alpha = 0.65f),
                fontSize = 12.sp,
                fontWeight = FontWeight.Medium,
                letterSpacing = 2.sp,
            )
        }

        Column(
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .fillMaxWidth()
                .padding(horizontal = 40.dp, vertical = 48.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
        ) {
            LinearProgressIndicator(
                progress = { progress.coerceIn(0f, 1f) },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(6.dp),
                color = SplashBlue,
                trackColor = Color.White.copy(alpha = 0.12f),
                strokeCap = StrokeCap.Round,
                gapSize = 0.dp,
                drawStopIndicator = {},
            )
            Spacer(modifier = Modifier.height(12.dp))
            Text(
                text = stringResource(Res.string.splash_initializing),
                color = SplashSilver.copy(alpha = 0.55f),
                fontSize = 13.sp,
            )
        }
    }
}

@Composable
private fun SplashLogo(modifier: Modifier = Modifier) {
    val infiniteTransition = rememberInfiniteTransition(label = "splash_orbit")
    val outerRotation by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = 360f,
        animationSpec = infiniteRepeatable(
            animation = tween(durationMillis = 5000, easing = LinearEasing),
            repeatMode = RepeatMode.Restart,
        ),
        label = "outer_rotation",
    )
    val innerRotation by infiniteTransition.animateFloat(
        initialValue = 360f,
        targetValue = 0f,
        animationSpec = infiniteRepeatable(
            animation = tween(durationMillis = 3500, easing = LinearEasing),
            repeatMode = RepeatMode.Restart,
        ),
        label = "inner_rotation",
    )

    Box(modifier = modifier, contentAlignment = Alignment.Center) {
        Canvas(modifier = Modifier.fillMaxSize()) {
            val center = Offset(size.width / 2f, size.height / 2f)
            val orbitRadius = size.minDimension * 0.34f

            rotate(outerRotation, center) {
                drawOrbitRing(
                    center = center,
                    radius = orbitRadius,
                    color = SplashBlue.copy(alpha = 0.85f),
                    strokeWidth = 5f,
                    startAngle = 20f,
                    sweepAngle = 300f,
                )
            }

            rotate(innerRotation, center) {
                drawOrbitRing(
                    center = center,
                    radius = orbitRadius * 0.82f,
                    color = SplashGold.copy(alpha = 0.9f),
                    strokeWidth = 4f,
                    startAngle = 180f,
                    sweepAngle = 250f,
                )
            }

            drawCircle(
                brush = Brush.radialGradient(
                    colors = listOf(
                        SplashBlue.copy(alpha = 0.25f),
                        Color.Transparent,
                    ),
                    center = center,
                    radius = size.minDimension * 0.22f,
                ),
                radius = size.minDimension * 0.22f,
                center = center,
            )
        }

        PinBadge()
    }
}

private fun androidx.compose.ui.graphics.drawscope.DrawScope.drawOrbitRing(
    center: Offset,
    radius: Float,
    color: Color,
    strokeWidth: Float,
    startAngle: Float,
    sweepAngle: Float,
) {
    drawArc(
        color = color,
        startAngle = startAngle,
        sweepAngle = sweepAngle,
        useCenter = false,
        topLeft = Offset(center.x - radius, center.y - radius),
        size = androidx.compose.ui.geometry.Size(radius * 2, radius * 2),
        style = Stroke(width = strokeWidth, cap = StrokeCap.Round),
    )
}

@Composable
private fun PinBadge() {
    Box(
        modifier = Modifier
            .size(96.dp)
            .background(
                brush = Brush.linearGradient(
                    colors = listOf(Color(0xFF2A3444), Color(0xFF1A2332)),
                ),
                shape = RoundedCornerShape(28.dp),
            ),
        contentAlignment = Alignment.Center,
    ) {
        Canvas(modifier = Modifier.size(72.dp)) {
            val pinPath = Path().apply {
                moveTo(size.width * 0.5f, size.height * 0.92f)
                cubicTo(
                    size.width * 0.15f, size.height * 0.55f,
                    size.width * 0.15f, size.height * 0.25f,
                    size.width * 0.5f, size.height * 0.18f,
                )
                cubicTo(
                    size.width * 0.85f, size.height * 0.25f,
                    size.width * 0.85f, size.height * 0.55f,
                    size.width * 0.5f, size.height * 0.92f,
                )
                close()
            }
            drawPath(
                path = pinPath,
                brush = Brush.verticalGradient(
                    colors = listOf(SplashBlue, Color(0xFF2563EB)),
                ),
            )
        }
        Text(
            text = "SP",
            color = SplashSilver,
            fontWeight = FontWeight.Bold,
            fontSize = 22.sp,
        )
    }
}
