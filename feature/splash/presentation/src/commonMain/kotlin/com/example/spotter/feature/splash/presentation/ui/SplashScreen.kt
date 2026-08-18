package com.example.spotter.feature.splash.presentation.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.spotter.feature.splash.presentation.generated.resources.Res
import com.example.spotter.feature.splash.presentation.generated.resources.splash_tagline
import com.example.spotter.feature.splash.presentation.platform.LocationPermissionEffect
import org.jetbrains.compose.resources.stringResource
import org.koin.compose.viewmodel.koinViewModel

private val SplashBackground = Color(0xFF000000)
private val SplashSilver = Color(0xFFF0F0F0)
private val SplashGold = Color(0xFFC5A059)
private val SplashIndicatorInactive = Color(0xFF3A3A3A)

@Composable
fun SplashScreen(
    onNavigateHome: () -> Unit,
    viewModel: SplashViewModel = koinViewModel(),
) {
    LocationPermissionEffect(onPermissionResolved = viewModel::startPreloadIfNeeded)

    LaunchedEffect(viewModel) {
        viewModel.navigateHome.collect {
            onNavigateHome()
        }
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(SplashBackground),
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 32.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center,
        ) {
            SplashLogo()
            Spacer(modifier = Modifier.height(24.dp))
            Text(
                text = "SPOTTER",
                color = SplashSilver,
                fontSize = 36.sp,
                fontWeight = FontWeight.Bold,
                letterSpacing = 4.sp,
            )
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = stringResource(Res.string.splash_tagline),
                color = SplashSilver.copy(alpha = 0.7f),
                fontSize = 12.sp,
                fontWeight = FontWeight.Normal,
                letterSpacing = 4.sp,
            )
        }

        SplashPageIndicators(
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .padding(bottom = 48.dp),
        )
    }
}

@Composable
private fun SplashPageIndicators(
    modifier: Modifier = Modifier,
) {
    Row(
        modifier = modifier,
        horizontalArrangement = Arrangement.spacedBy(8.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        repeat(4) { index ->
            Box(
                modifier = Modifier
                    .width(if (index == 0) 26.dp else 18.dp)
                    .height(3.dp)
                    .clip(RoundedCornerShape(50))
                    .background(
                        if (index == 0) SplashGold else SplashIndicatorInactive,
                    ),
            )
        }
    }
}
