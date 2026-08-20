package com.example.spotter.feature.splash.presentation.platform

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import com.example.spotter.core.location.LocationPlatform

@Composable
actual fun LocationPermissionEffect(
    onPermissionResolved: () -> Unit,
) {
    LaunchedEffect(Unit) {
        LocationPlatform.requestWhenInUsePermission()
        onPermissionResolved()
    }
}
