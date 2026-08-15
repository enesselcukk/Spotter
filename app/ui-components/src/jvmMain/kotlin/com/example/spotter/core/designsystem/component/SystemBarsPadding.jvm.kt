package com.example.spotter.core.designsystem.component

import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.navigationBars
import androidx.compose.foundation.layout.statusBars
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier

@Composable
actual fun Modifier.spotterStatusBarsPadding(): Modifier =
    windowInsetsPadding(WindowInsets.statusBars)

@Composable
actual fun Modifier.spotterNavigationBarsPadding(): Modifier =
    windowInsetsPadding(WindowInsets.navigationBars)
