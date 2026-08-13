package com.example.spotter.feature.splash.presentation.platform

import androidx.compose.runtime.Composable

@Composable
expect fun LocationPermissionEffect(
    onPermissionResolved: () -> Unit,
)
