package com.example.spotter.feature.home.presentation.platform

import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember

@Composable
actual fun rememberDirectionsLauncher(): (latitude: Double, longitude: Double, label: String?) -> Unit =
    remember { { _, _, _ -> } }
