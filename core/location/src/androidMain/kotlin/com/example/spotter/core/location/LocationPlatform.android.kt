package com.example.spotter.core.location

import android.Manifest
import android.annotation.SuppressLint
import android.content.Context
import android.content.pm.PackageManager
import android.location.Geocoder
import android.location.Location
import android.os.Looper
import com.google.android.gms.location.CurrentLocationRequest
import com.google.android.gms.location.LocationCallback
import com.google.android.gms.location.LocationRequest
import com.google.android.gms.location.LocationResult
import com.google.android.gms.location.LocationServices
import com.google.android.gms.location.Priority
import com.google.android.gms.tasks.CancellationTokenSource
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.flow.emptyFlow
import kotlinx.coroutines.suspendCancellableCoroutine
import kotlinx.coroutines.withContext
import kotlinx.coroutines.withTimeoutOrNull
import java.util.Locale
import kotlin.coroutines.resume

actual object LocationPlatform {

    private var applicationContext: Context? = null

    fun initialize(context: Context) {
        applicationContext = context.applicationContext
    }

    actual fun permissionStatus(): LocationPermissionStatus {
        val context = requireContext()
        val fineGranted = context.checkSelfPermission(Manifest.permission.ACCESS_FINE_LOCATION) ==
            PackageManager.PERMISSION_GRANTED
        val coarseGranted = context.checkSelfPermission(Manifest.permission.ACCESS_COARSE_LOCATION) ==
            PackageManager.PERMISSION_GRANTED

        return if (fineGranted || coarseGranted) {
            LocationPermissionStatus.Granted
        } else {
            LocationPermissionStatus.Denied
        }
    }

    actual suspend fun requestWhenInUsePermission(): LocationPermissionStatus =
        permissionStatus()

    @SuppressLint("MissingPermission")
    actual suspend fun currentLocation(timeoutMs: Long): GeoCoordinates? {
        if (!permissionStatus().isGranted()) return null

        val location = fetchLocation(timeoutMs) ?: return null
        return GeoCoordinates(
            latitude = location.latitude,
            longitude = location.longitude,
        )
    }

    @SuppressLint("MissingPermission")
    actual fun locationUpdates(): Flow<GeoCoordinates> {
        if (!permissionStatus().isGranted()) return emptyFlow()

        val context = requireContext()
        return callbackFlow {
            val client = LocationServices.getFusedLocationProviderClient(context)
            val callback = object : LocationCallback() {
                override fun onLocationResult(result: LocationResult) {
                    val location = result.lastLocation ?: return
                    trySend(
                        GeoCoordinates(
                            latitude = location.latitude,
                            longitude = location.longitude,
                        ),
                    )
                }
            }
            val request = LocationRequest.Builder(Priority.PRIORITY_HIGH_ACCURACY, 1_000L)
                .setMinUpdateIntervalMillis(500L)
                .setWaitForAccurateLocation(false)
                .build()

            client.lastLocation.addOnSuccessListener { location ->
                if (location != null) {
                    trySend(
                        GeoCoordinates(
                            latitude = location.latitude,
                            longitude = location.longitude,
                        ),
                    )
                }
            }
            client.requestLocationUpdates(request, callback, Looper.getMainLooper())

            awaitClose {
                client.removeLocationUpdates(callback)
            }
        }
    }

    actual suspend fun reverseGeocode(latitude: Double, longitude: Double): String? =
        withContext(Dispatchers.IO) {
            val context = applicationContext ?: return@withContext null
            if (!Geocoder.isPresent()) return@withContext null

            runCatching {
                @Suppress("DEPRECATION")
                Geocoder(context, Locale.getDefault())
                    .getFromLocation(latitude, longitude, 1)
                    ?.firstOrNull()
                    ?.toShortLabel()
            }.getOrNull()
        }

    @SuppressLint("MissingPermission")
    private suspend fun fetchLocation(timeoutMs: Long): Location? =
        withTimeoutOrNull(timeoutMs) {
            suspendCancellableCoroutine { continuation ->
                val client = LocationServices.getFusedLocationProviderClient(requireContext())

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

    private fun requireContext(): Context =
        applicationContext ?: error("LocationPlatform.initialize(context) must be called before use")

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
