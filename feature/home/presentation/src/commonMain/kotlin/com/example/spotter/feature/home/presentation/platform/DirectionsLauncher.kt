package com.example.spotter.feature.home.presentation.platform

import androidx.compose.runtime.Composable

@Composable
expect fun rememberDirectionsLauncher(): (latitude: Double, longitude: Double, label: String?) -> Unit
