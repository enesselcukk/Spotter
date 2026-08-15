package com.example.spotter.feature.map.presentation.platform

import androidx.compose.runtime.Composable

@Composable
expect fun MapBackHandler(
    enabled: Boolean,
    onBack: () -> Unit,
)
