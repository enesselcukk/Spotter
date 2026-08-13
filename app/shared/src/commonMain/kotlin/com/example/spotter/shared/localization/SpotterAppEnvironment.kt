package com.example.spotter.shared.localization

import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider

@Composable
fun SpotterAppEnvironment(
    languageTag: String?,
    darkTheme: Boolean?,
    content: @Composable () -> Unit,
) {
    CompositionLocalProvider(
        LocalAppLocale provides languageTag,
        LocalAppTheme provides darkTheme,
        content = content,
    )
}
