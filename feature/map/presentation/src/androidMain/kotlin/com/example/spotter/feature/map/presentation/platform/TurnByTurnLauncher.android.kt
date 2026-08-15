package com.example.spotter.feature.map.presentation.platform

import android.content.Intent
import android.net.Uri
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.platform.LocalContext

@Composable
actual fun rememberTurnByTurnLauncher(): (
    latitude: Double,
    longitude: Double,
    label: String?,
) -> Unit {
    val context = LocalContext.current
    return remember(context) {
        { latitude, longitude, label ->
            val navigationIntent = Intent(
                Intent.ACTION_VIEW,
                Uri.parse("google.navigation:q=$latitude,$longitude&mode=d"),
            )
            val fallbackQuery = buildString {
                append(latitude)
                append(',')
                append(longitude)
                if (!label.isNullOrBlank()) {
                    append('(')
                    append(label)
                    append(')')
                }
            }
            val fallbackIntent = Intent(
                Intent.ACTION_VIEW,
                Uri.parse("geo:0,0?q=$fallbackQuery"),
            )

            val intent = if (navigationIntent.resolveActivity(context.packageManager) != null) {
                navigationIntent
            } else {
                fallbackIntent
            }
            context.startActivity(intent)
        }
    }
}
