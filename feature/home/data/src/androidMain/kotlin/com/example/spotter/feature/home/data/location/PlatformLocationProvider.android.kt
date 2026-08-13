package com.example.spotter.feature.home.data.location

import android.annotation.SuppressLint
import android.content.Context
import android.content.pm.PackageManager
import android.location.Geocoder
import android.location.Location
import com.example.spotter.feature.home.domain.model.DeviceLocation
import com.google.android.gms.location.CurrentLocationRequest
import com.google.android.gms.location.LocationServices
import com.google.android.gms.location.Priority
import com.google.android.gms.tasks.CancellationTokenSource
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import kotlinx.coroutines.withTimeoutOrNull
import kotlinx.coroutines.suspendCancellableCoroutine
import java.util.Locale
import kotlin.coroutines.resume
import android.Manifest

actual class PlatformLocationProvider(
    private val context: Context,
) {

    actual fun hasLocationPermission(): Boolean =
        context.checkSelfPermission(Manifest.permission.ACCESS_FINE_LOCATION) ==
            PackageManager.PERMISSION_GRANTED ||
            context.checkSelfPermission(Manifest.permission.ACCESS_COARSE_LOCATION) ==
                PackageManager.PERMISSION_GRANTED

    @SuppressLint("MissingPermission")
    actual suspend fun getDeviceLocation(): DeviceLocation? {
        if (!hasLocationPermission()) return null

        val location = fetchLocation() ?: return null
        val label = reverseGeocode(location.latitude, location.longitude)
        return DeviceLocation(
            latitude = location.latitude,
            longitude = location.longitude,
            label = label,
        )
    }

    @SuppressLint("MissingPermission")
    private suspend fun fetchLocation(): Location? =
        withTimeoutOrNull(LOCATION_TIMEOUT_MS) {
            suspendCancellableCoroutine { continuation ->
                val client = LocationServices.getFusedLocationProviderClient(context)

                client.lastLocation
                    .addOnSuccessListener { lastLocation ->
                        if (lastLocation != null) {
                            continuation.resume(lastLocation)
                            return@addOnSuccessListener
                        }

                        val request = CurrentLocationRequest.Builder()
                            .setPriority(Priority.PRIORITY_BALANCED_POWER_ACCURACY)
                            .setMaxUpdateAgeMillis(60_000L)
                            .build()

                        client.getCurrentLocation(request, CancellationTokenSource().token)
                            .addOnSuccessListener { currentLocation ->
                                continuation.resume(currentLocation)
                            }
                            .addOnFailureListener {
                                continuation.resume(null)
                            }
                    }
                    .addOnFailureListener {
                        continuation.resume(null)
                    }
            }
        }

    private companion object {
        const val LOCATION_TIMEOUT_MS = 10_000L
    }

    private suspend fun reverseGeocode(latitude: Double, longitude: Double): String? =
        withContext(Dispatchers.IO) {
            if (!Geocoder.isPresent()) return@withContext null

            runCatching {
                @Suppress("DEPRECATION")
                Geocoder(context, Locale.getDefault())
                    .getFromLocation(latitude, longitude, 1)
                    ?.firstOrNull()
                    ?.toShortLabel()
            }.getOrNull()
        }

    private fun android.location.Address.toShortLabel(): String? {
        val district = subLocality ?: subAdminArea ?: locality
        val city = adminArea ?: locality
        return listOfNotNull(district, city)
            .distinct()
            .joinToString(", ")
            .ifBlank { locality ?: thoroughfare }
            ?.ifBlank { null }
    }
}
