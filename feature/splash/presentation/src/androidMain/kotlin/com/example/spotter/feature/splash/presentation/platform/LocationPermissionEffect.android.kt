package com.example.spotter.feature.splash.presentation.platform

import android.Manifest
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import com.example.spotter.core.location.LocationPlatform
import com.example.spotter.core.location.isGranted

@Composable
actual fun LocationPermissionEffect(
    onPermissionResolved: () -> Unit,
) {
    val permissionLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestMultiplePermissions(),
        onResult = { onPermissionResolved() },
    )

    LaunchedEffect(Unit) {
        if (LocationPlatform.permissionStatus().isGranted()) {
            onPermissionResolved()
        } else {
            permissionLauncher.launch(
                arrayOf(
                    Manifest.permission.ACCESS_FINE_LOCATION,
                    Manifest.permission.ACCESS_COARSE_LOCATION,
                ),
            )
        }
    }
}
