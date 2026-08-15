package com.example.spotter.feature.home.presentation.ui

import androidx.compose.runtime.Composable
import com.example.spotter.feature.home.presentation.generated.resources.Res
import com.example.spotter.feature.home.presentation.generated.resources.home_location_current
import com.example.spotter.feature.home.presentation.generated.resources.home_location_fallback
import com.example.spotter.feature.home.presentation.generated.resources.home_location_loading
import org.jetbrains.compose.resources.stringResource

@Composable
fun resolveLocationLabel(
    locationLabel: String?,
    usesDeviceLocation: Boolean,
): String = when {
    !locationLabel.isNullOrBlank() -> locationLabel
    locationLabel == null -> stringResource(Res.string.home_location_loading)
    usesDeviceLocation -> stringResource(Res.string.home_location_current)
    else -> stringResource(Res.string.home_location_fallback)
}
