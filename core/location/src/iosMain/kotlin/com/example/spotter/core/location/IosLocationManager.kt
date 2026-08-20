package com.example.spotter.core.location

import kotlinx.cinterop.ExperimentalForeignApi
import kotlinx.cinterop.useContents
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.suspendCancellableCoroutine
import kotlinx.coroutines.withTimeoutOrNull
import platform.CoreLocation.CLAuthorizationStatus
import platform.CoreLocation.CLLocation
import platform.CoreLocation.CLLocationManager
import platform.CoreLocation.CLLocationManagerDelegateProtocol
import platform.CoreLocation.CLPlacemark
import platform.CoreLocation.kCLAuthorizationStatusAuthorizedAlways
import platform.CoreLocation.kCLAuthorizationStatusAuthorizedWhenInUse
import platform.CoreLocation.kCLAuthorizationStatusDenied
import platform.CoreLocation.kCLAuthorizationStatusNotDetermined
import platform.CoreLocation.kCLAuthorizationStatusRestricted
import platform.CoreLocation.kCLLocationAccuracyBest
import platform.Foundation.NSError
import platform.darwin.NSObject
import kotlin.coroutines.resume

@OptIn(ExperimentalForeignApi::class)
object IosLocationManager {

    private const val DEFAULT_LOCATION_TIMEOUT_MS = 10_000L

    fun currentPermissionStatus(): LocationPermissionStatus =
        CLLocationManager().authorizationStatus.toPermissionStatus()

    suspend fun requestWhenInUsePermission(): LocationPermissionStatus {
        val currentStatus = currentPermissionStatus()
        if (currentStatus != LocationPermissionStatus.NotDetermined) {
            return currentStatus
        }

        return suspendCancellableCoroutine { continuation ->
            val manager = CLLocationManager()
            val delegate = object : NSObject(), CLLocationManagerDelegateProtocol {
                override fun locationManagerDidChangeAuthorization(manager: CLLocationManager) {
                    val status = manager.authorizationStatus.toPermissionStatus()
                    if (status == LocationPermissionStatus.NotDetermined) return

                    manager.delegate = null
                    if (continuation.isActive) {
                        continuation.resume(status)
                    }
                }
            }

            manager.delegate = delegate
            manager.requestWhenInUseAuthorization()

            continuation.invokeOnCancellation {
                manager.delegate = null
            }
        }
    }

    suspend fun currentLocation(timeoutMs: Long = DEFAULT_LOCATION_TIMEOUT_MS): GeoCoordinates? {
        if (!currentPermissionStatus().isGranted()) return null

        return withTimeoutOrNull(timeoutMs) {
            suspendCancellableCoroutine { continuation ->
                val manager = CLLocationManager()
                val delegate = object : NSObject(), CLLocationManagerDelegateProtocol {
                    override fun locationManager(manager: CLLocationManager, didUpdateLocations: List<*>) {
                        val location = didUpdateLocations.lastOrNull() as? CLLocation ?: return
                        manager.stopUpdatingLocation()
                        manager.delegate = null
                        if (continuation.isActive) {
                            continuation.resume(location.toGeoCoordinates())
                        }
                    }

                    override fun locationManager(manager: CLLocationManager, didFailWithError: NSError) {
                        manager.stopUpdatingLocation()
                        manager.delegate = null
                        if (continuation.isActive) {
                            continuation.resume(null)
                        }
                    }
                }

                manager.delegate = delegate
                manager.desiredAccuracy = kCLLocationAccuracyBest
                manager.requestLocation()

                continuation.invokeOnCancellation {
                    manager.stopUpdatingLocation()
                    manager.delegate = null
                }
            }
        }
    }

    fun locationUpdates(): Flow<GeoCoordinates> = callbackFlow {
        if (!currentPermissionStatus().isGranted()) {
            close()
            return@callbackFlow
        }

        val manager = CLLocationManager()
        val delegate = object : NSObject(), CLLocationManagerDelegateProtocol {
            override fun locationManager(manager: CLLocationManager, didUpdateLocations: List<*>) {
                val location = didUpdateLocations.lastOrNull() as? CLLocation ?: return
                trySend(location.toGeoCoordinates())
            }

            override fun locationManagerDidChangeAuthorization(manager: CLLocationManager) {
                when (manager.authorizationStatus.toPermissionStatus()) {
                    LocationPermissionStatus.Granted -> manager.startUpdatingLocation()
                    else -> close()
                }
            }
        }

        manager.delegate = delegate
        manager.desiredAccuracy = kCLLocationAccuracyBest

        if (currentPermissionStatus().isGranted()) {
            manager.startUpdatingLocation()
        } else if (currentPermissionStatus() == LocationPermissionStatus.NotDetermined) {
            manager.requestWhenInUseAuthorization()
        } else {
            close()
            return@callbackFlow
        }

        awaitClose {
            manager.stopUpdatingLocation()
            manager.delegate = null
        }
    }

    suspend fun reverseGeocode(latitude: Double, longitude: Double): String? =
        suspendCancellableCoroutine { continuation ->
            val geocoder = platform.CoreLocation.CLGeocoder()
            val location = CLLocation(latitude = latitude, longitude = longitude)

            geocoder.reverseGeocodeLocation(location) { placemarks, _ ->
                val label = (placemarks?.firstOrNull() as? CLPlacemark)?.toShortLabel()
                if (continuation.isActive) {
                    continuation.resume(label)
                }
            }
        }
}

@OptIn(ExperimentalForeignApi::class)
private fun CLAuthorizationStatus.toPermissionStatus(): LocationPermissionStatus = when (this) {
    kCLAuthorizationStatusAuthorizedWhenInUse,
    kCLAuthorizationStatusAuthorizedAlways,
    -> LocationPermissionStatus.Granted

    kCLAuthorizationStatusDenied -> LocationPermissionStatus.Denied
    kCLAuthorizationStatusRestricted -> LocationPermissionStatus.Restricted
    else -> LocationPermissionStatus.NotDetermined
}

@OptIn(ExperimentalForeignApi::class)
private fun CLLocation.toGeoCoordinates(): GeoCoordinates {
    val coordinate = coordinate
    return GeoCoordinates(
        latitude = coordinate.useContents { latitude },
        longitude = coordinate.useContents { longitude },
    )
}

@OptIn(ExperimentalForeignApi::class)
private fun CLPlacemark.toShortLabel(): String? {
    val district = subLocality ?: subAdministrativeArea ?: locality
    val city = administrativeArea ?: locality
    return listOfNotNull(district, city)
        .distinct()
        .joinToString(", ")
        .ifBlank { locality ?: thoroughfare }
        ?.ifBlank { null }
}
