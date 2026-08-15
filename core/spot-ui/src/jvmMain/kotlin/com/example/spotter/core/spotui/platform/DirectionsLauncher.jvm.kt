package com.example.spotter.core.spotui.platform

import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember

@Composable
actual fun rememberDirectionsLauncher(): (latitude: Double, longitude: Double, label: String?) -> Unit =
    remember { { _, _, _ -> } }
