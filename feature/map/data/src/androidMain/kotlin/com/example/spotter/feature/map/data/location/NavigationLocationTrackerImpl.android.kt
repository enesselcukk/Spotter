package com.example.spotter.feature.map.data.location

import android.Manifest
import android.annotation.SuppressLint
import android.content.Context
import android.content.pm.PackageManager
import android.os.Looper
import com.example.spotter.feature.map.domain.model.RoutePoint
import com.example.spotter.feature.map.domain.repository.NavigationLocationTracker
import com.google.android.gms.location.LocationCallback
import com.google.android.gms.location.LocationRequest
import com.google.android.gms.location.LocationResult
import com.google.android.gms.location.LocationServices
import com.google.android.gms.location.Priority
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow

actual class NavigationLocationTrackerImpl(
    private val context: Context,
) : NavigationLocationTracker {

    @SuppressLint("MissingPermission")
    actual override fun locations(): Flow<RoutePoint> = callbackFlow {
        if (!hasLocationPermission()) {
            close()
            return@callbackFlow
        }

        val client = LocationServices.getFusedLocationProviderClient(context)
        val callback = object : LocationCallback() {
            override fun onLocationResult(result: LocationResult) {
                val location = result.lastLocation ?: return
                trySend(RoutePoint(location.latitude, location.longitude))
            }
        }
        val request = LocationRequest.Builder(Priority.PRIORITY_HIGH_ACCURACY, 1_000L)
            .setMinUpdateIntervalMillis(500L)
            .setWaitForAccurateLocation(false)
            .build()

        client.lastLocation.addOnSuccessListener { location ->
            if (location != null) {
                trySend(RoutePoint(location.latitude, location.longitude))
            }
        }
        client.requestLocationUpdates(request, callback, Looper.getMainLooper())

        awaitClose {
            client.removeLocationUpdates(callback)
        }
    }

    private fun hasLocationPermission(): Boolean =
        context.checkSelfPermission(Manifest.permission.ACCESS_FINE_LOCATION) ==
            PackageManager.PERMISSION_GRANTED ||
            context.checkSelfPermission(Manifest.permission.ACCESS_COARSE_LOCATION) ==
                PackageManager.PERMISSION_GRANTED
}
