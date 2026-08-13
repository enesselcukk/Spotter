package com.example.spotter.feature.splash.presentation.platform

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect

@Composable
actual fun LocationPermissionEffect(
    onPermissionResolved: () -> Unit,
) {
    LaunchedEffect(Unit) {
        onPermissionResolved()
    }
}
