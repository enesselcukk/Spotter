package com.example.spotter.core.spotui.platform

import android.content.Intent
import android.net.Uri
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.platform.LocalContext

@Composable
actual fun rememberDirectionsLauncher(): (latitude: Double, longitude: Double, label: String?) -> Unit {
    val context = LocalContext.current
    return remember(context) {
        { latitude, longitude, label ->
            val query = buildString {
                append(latitude)
                append(',')
                append(longitude)
                if (!label.isNullOrBlank()) {
                    append('(')
                    append(label)
                    append(')')
                }
            }
            val intent = Intent(
                Intent.ACTION_VIEW,
                Uri.parse("geo:0,0?q=$query"),
            )
            context.startActivity(intent)
        }
    }
}
