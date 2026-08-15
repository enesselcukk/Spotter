package com.example.spotter.core.designsystem.component

import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

@Composable
actual fun Modifier.spotterStatusBarsPadding(): Modifier = padding(top = 54.dp)

@Composable
actual fun Modifier.spotterNavigationBarsPadding(): Modifier = padding(bottom = 34.dp)
