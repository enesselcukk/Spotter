package com.example.spotter.feature.map.data.location

import com.example.spotter.feature.map.domain.model.RoutePoint
import com.example.spotter.feature.map.domain.repository.NavigationLocationTracker
import kotlinx.cinterop.ExperimentalForeignApi
import kotlinx.cinterop.useContents
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import platform.CoreLocation.CLLocation
import platform.CoreLocation.CLLocationManager
import platform.CoreLocation.CLLocationManagerDelegateProtocol
import platform.CoreLocation.kCLLocationAccuracyBest
import platform.darwin.NSObject

@OptIn(ExperimentalForeignApi::class)
actual class NavigationLocationTrackerImpl : NavigationLocationTracker {

    actual override fun locations(): Flow<RoutePoint> = callbackFlow {
        val manager = CLLocationManager()
        val delegate = LocationDelegate { point ->
            trySend(point)
        }
        manager.delegate = delegate
        manager.desiredAccuracy = kCLLocationAccuracyBest
        manager.requestWhenInUseAuthorization()
        manager.startUpdatingLocation()

        awaitClose {
            manager.stopUpdatingLocation()
            manager.delegate = null
            delegate.hashCode()
        }
    }
}

@OptIn(ExperimentalForeignApi::class)
private class LocationDelegate(
    private val onLocation: (RoutePoint) -> Unit,
) : NSObject(), CLLocationManagerDelegateProtocol {

    override fun locationManager(manager: CLLocationManager, didUpdateLocations: List<*>) {
        val location = didUpdateLocations.lastOrNull() as? CLLocation ?: return
        val coordinate = location.coordinate
        onLocation(
            RoutePoint(
                latitude = coordinate.useContents { latitude },
                longitude = coordinate.useContents { longitude },
            ),
        )
    }
}
