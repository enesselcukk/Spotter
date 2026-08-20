package com.example.spotter.feature.splash.presentation.platform

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import com.example.spotter.core.location.IosLocationManager

@Composable
actual fun LocationPermissionEffect(
    onPermissionResolved: () -> Unit,
) {
    LaunchedEffect(Unit) {
        IosLocationManager.requestWhenInUsePermission()
        onPermissionResolved()
    }
}
