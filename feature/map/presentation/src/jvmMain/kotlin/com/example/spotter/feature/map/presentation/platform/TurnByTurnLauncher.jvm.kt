package com.example.spotter.feature.map.presentation.platform

import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember

@Composable
actual fun rememberTurnByTurnLauncher(): (
    latitude: Double,
    longitude: Double,
    label: String?,
) -> Unit = remember { { _, _, _ -> } }
