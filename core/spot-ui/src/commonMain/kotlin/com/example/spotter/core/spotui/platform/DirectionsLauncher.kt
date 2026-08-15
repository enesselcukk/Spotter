package com.example.spotter.core.spotui.platform

import androidx.compose.runtime.Composable

@Composable
expect fun rememberDirectionsLauncher(): (latitude: Double, longitude: Double, label: String?) -> Unit
