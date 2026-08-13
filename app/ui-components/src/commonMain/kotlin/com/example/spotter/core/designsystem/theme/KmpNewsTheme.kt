package com.example.spotter.core.designsystem.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.luminance

private val SpotterLightColorScheme = lightColorScheme(
    primary = SpotterLightPrimary,
    onPrimary = SpotterLightOnPrimary,
    primaryContainer = SpotterLightPrimaryContainer,
    onPrimaryContainer = SpotterLightOnPrimaryContainer,
    secondary = SpotterLightSecondary,
    onSecondary = SpotterLightOnPrimary,
    secondaryContainer = SpotterLightSurface,
    onSecondaryContainer = SpotterLightOnSurface,
    tertiary = SpotterLightPrimary,
    onTertiary = SpotterLightOnPrimary,
    background = SpotterLightBackground,
    onBackground = SpotterLightOnSurface,
    surface = SpotterLightSurface,
    onSurface = SpotterLightOnSurface,
    surfaceVariant = SpotterLightSurfaceVariant,
    onSurfaceVariant = SpotterLightOnSurfaceMuted,
    outline = SpotterLightOutline,
    outlineVariant = Color(0xFFD1D5DB),
    scrim = Color(0xFF000000),
)

private val SpotterDarkColorScheme = darkColorScheme(
    primary = SpotterDarkPrimary,
    onPrimary = SpotterDarkOnPrimary,
    primaryContainer = Color(0xFF5C4A00),
    onPrimaryContainer = SpotterDarkPrimary,
    secondary = SpotterBlue,
    onSecondary = Color.White,
    secondaryContainer = SpotterDarkSurface,
    onSecondaryContainer = SpotterDarkOnSurface,
    tertiary = SpotterDarkPrimary,
    onTertiary = SpotterDarkOnPrimary,
    background = SpotterDarkBackground,
    onBackground = SpotterDarkOnSurface,
    surface = SpotterDarkSurface,
    onSurface = SpotterDarkOnSurface,
    surfaceVariant = SpotterDarkSurfaceElevated,
    onSurfaceVariant = SpotterDarkOnSurfaceMuted,
    outline = Color(0xFF3D4A5C),
    outlineVariant = Color(0xFF2E3A4A),
    scrim = Color(0xFF000000),
    inverseSurface = SpotterDarkOnSurface,
    inverseOnSurface = SpotterDarkBackground,
)

@Composable
fun SpotterTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    content: @Composable () -> Unit,
) {
    MaterialTheme(
        colorScheme = if (darkTheme) SpotterDarkColorScheme else SpotterLightColorScheme,
        typography = SpotterTypography,
        content = content,
    )
}

@Composable
fun spotterMapOverlayColor(): Color =
    MaterialTheme.colorScheme.background.copy(alpha = 0.88f)

@Composable
fun spotterAccentColor(): Color = MaterialTheme.colorScheme.primary

@Composable
fun spotterDirectionsButtonColors() = ButtonDefaults.buttonColors(
    containerColor = if (MaterialTheme.colorScheme.background.luminance() < 0.5f) {
        MaterialTheme.colorScheme.secondary
    } else {
        MaterialTheme.colorScheme.primary
    },
    contentColor = if (MaterialTheme.colorScheme.background.luminance() < 0.5f) {
        MaterialTheme.colorScheme.onSecondary
    } else {
        MaterialTheme.colorScheme.onPrimary
    },
)

@Composable
fun spotterMapToggleButtonColors() = ButtonDefaults.buttonColors(
    containerColor = MaterialTheme.colorScheme.tertiary,
    contentColor = MaterialTheme.colorScheme.onTertiary,
)
