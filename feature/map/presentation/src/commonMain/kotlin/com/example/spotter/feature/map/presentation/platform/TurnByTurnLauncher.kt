package com.example.spotter.feature.map.presentation.platform

import androidx.compose.runtime.Composable

/**
 * Hands the route over to the platform navigation app (Google Maps, Yandex Maps, Apple Maps)
 * so the user can start guided navigation from the spot they picked here.
 */
@Composable
expect fun rememberTurnByTurnLauncher(): (
    latitude: Double,
    longitude: Double,
    label: String?,
) -> Unit
