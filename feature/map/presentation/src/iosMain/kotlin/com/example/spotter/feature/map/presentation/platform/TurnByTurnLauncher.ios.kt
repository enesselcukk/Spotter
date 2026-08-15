package com.example.spotter.feature.map.presentation.platform

import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import platform.Foundation.NSURL
import platform.UIKit.UIApplication

@Composable
actual fun rememberTurnByTurnLauncher(): (
    latitude: Double,
    longitude: Double,
    label: String?,
) -> Unit = remember {
    { latitude, longitude, _ ->
        val application = UIApplication.sharedApplication
        val googleMaps = NSURL.URLWithString("comgooglemaps://?daddr=$latitude,$longitude&directionsmode=driving")
        val appleMaps = NSURL.URLWithString("http://maps.apple.com/?daddr=$latitude,$longitude&dirflg=d")

        val target = googleMaps?.takeIf { application.canOpenURL(it) } ?: appleMaps
        target?.let { application.openURL(it, emptyMap<Any?, Any>(), null) }
    }
}
