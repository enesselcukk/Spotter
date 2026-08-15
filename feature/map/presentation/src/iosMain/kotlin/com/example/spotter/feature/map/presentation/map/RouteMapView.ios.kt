package com.example.spotter.feature.map.presentation.map

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.interop.UIKitView
import com.example.spotter.feature.home.domain.model.SpotDto
import com.example.spotter.feature.map.domain.model.RoutePoint
import kotlinx.cinterop.ExperimentalForeignApi
import kotlinx.cinterop.allocArray
import kotlinx.cinterop.get
import kotlinx.cinterop.memScoped
import platform.CoreLocation.CLLocationCoordinate2D
import platform.CoreLocation.CLLocationCoordinate2DMake
import platform.MapKit.MKAnnotationView
import platform.MapKit.addOverlay
import platform.MapKit.overlays
import platform.MapKit.removeOverlays
import platform.MapKit.MKCoordinateRegionMakeWithDistance
import platform.MapKit.MKMapView
import platform.MapKit.MKMapViewDelegateProtocol
import platform.MapKit.MKOverlayProtocol
import platform.MapKit.MKOverlayRenderer
import platform.MapKit.MKPointAnnotation
import platform.MapKit.MKPolyline
import platform.MapKit.MKPolylineRenderer
import platform.MapKit.MKUserTrackingModeNone
import platform.UIKit.UIColor
import platform.UIKit.UIEdgeInsetsMake

private const val DefaultRadiusMeters = 1_500.0
private const val FollowRadiusMeters = 180.0
private const val MinRadiusMeters = 120.0
private const val MaxRadiusMeters = 400_000.0

@OptIn(ExperimentalForeignApi::class)
@Composable
actual fun RouteMapView(
    userLocation: RoutePoint,
    spots: List<SpotDto>,
    selectedSpotId: Long?,
    routeGeometry: List<RoutePoint>,
    mapState: RouteMapState,
    onSpotSelected: (Long) -> Unit,
    modifier: Modifier,
    followUser: Boolean,
) {
    val camera = remember { CameraState() }
    val delegate = remember { RouteMapDelegate() }
    delegate.onSpotSelected = onSpotSelected

    val mapView = remember {
        MKMapView().apply {
            this.delegate = delegate
            showsUserLocation = false
            showsCompass = true
            showsScale = true
            userTrackingMode = MKUserTrackingModeNone
        }
    }

    val userAnnotation = remember { MKPointAnnotation() }

    LaunchedEffect(mapView, spots, selectedSpotId, routeGeometry) {
        mapView.removeAnnotations(mapView.annotations)
        mapView.removeOverlays(mapView.overlays)

        spots.forEach { spot ->
            mapView.addAnnotation(
                SpotAnnotation(spot.id).apply {
                    setCoordinate(CLLocationCoordinate2DMake(spot.lat, spot.lon))
                    setTitle(spot.name)
                },
            )
        }

        userAnnotation.setCoordinate(
            CLLocationCoordinate2DMake(userLocation.latitude, userLocation.longitude),
        )
        mapView.addAnnotation(userAnnotation)

        val polyline = routeGeometry.toPolyline()
        if (polyline != null) {
            mapView.addOverlay(polyline)
            if (!followUser) {
                mapView.setVisibleMapRect(
                    polyline.boundingMapRect,
                    edgePadding = UIEdgeInsetsMake(96.0, 48.0, 280.0, 48.0),
                    animated = true,
                )
            }
        } else if (!followUser) {
            camera.radiusMeters = DefaultRadiusMeters
            mapView.focusOn(userLocation, camera.radiusMeters)
        }
    }

    LaunchedEffect(mapView, userLocation, followUser) {
        userAnnotation.setCoordinate(
            CLLocationCoordinate2DMake(userLocation.latitude, userLocation.longitude),
        )
        if (followUser) {
            camera.radiusMeters = FollowRadiusMeters
            mapView.focusOn(userLocation, camera.radiusMeters)
        }
    }

    LaunchedEffect(mapState.pendingCommand) {
        val command = mapState.pendingCommand ?: return@LaunchedEffect
        when (command.camera) {
            MapCamera.FollowUser -> {
                camera.radiusMeters = if (followUser) FollowRadiusMeters else DefaultRadiusMeters
                mapView.focusOn(userLocation, camera.radiusMeters)
            }

            MapCamera.FitRoute -> {
                routeGeometry.toPolyline()?.let { polyline ->
                    mapView.setVisibleMapRect(
                        polyline.boundingMapRect,
                        edgePadding = UIEdgeInsetsMake(96.0, 48.0, 280.0, 48.0),
                        animated = true,
                    )
                }
            }

            MapCamera.ZoomIn -> {
                camera.scaleBy(0.5)
                mapView.focusOnCenter(camera.radiusMeters)
            }

            MapCamera.ZoomOut -> {
                camera.scaleBy(2.0)
                mapView.focusOnCenter(camera.radiusMeters)
            }
        }
        mapState.consume()
    }

    UIKitView(
        factory = { mapView },
        modifier = modifier.fillMaxSize(),
        update = { },
    )
}

private class CameraState {
    var radiusMeters: Double = DefaultRadiusMeters

    fun scaleBy(factor: Double) {
        radiusMeters = (radiusMeters * factor).coerceIn(MinRadiusMeters, MaxRadiusMeters)
    }
}

private class SpotAnnotation(val spotId: Long) : MKPointAnnotation()

private class RouteMapDelegate : platform.darwin.NSObject(), MKMapViewDelegateProtocol {

    var onSpotSelected: (Long) -> Unit = {}

    override fun mapView(mapView: MKMapView, rendererForOverlay: MKOverlayProtocol): MKOverlayRenderer {
        if (rendererForOverlay is MKPolyline) {
            return MKPolylineRenderer(polyline = rendererForOverlay).apply {
                strokeColor = UIColor(red = 0.26, green = 0.52, blue = 0.96, alpha = 1.0)
                lineWidth = 6.0
            }
        }
        return MKOverlayRenderer(overlay = rendererForOverlay)
    }

    override fun mapView(mapView: MKMapView, didSelectAnnotationView: MKAnnotationView) {
        val annotation = didSelectAnnotationView.annotation as? SpotAnnotation ?: return
        onSpotSelected(annotation.spotId)
    }
}

@OptIn(ExperimentalForeignApi::class)
private fun List<RoutePoint>.toPolyline(): MKPolyline? {
    if (size < 2) return null
    return memScoped {
        val coordinates = allocArray<CLLocationCoordinate2D>(size)
        forEachIndexed { index, point ->
            coordinates[index].latitude = point.latitude
            coordinates[index].longitude = point.longitude
        }
        MKPolyline.polylineWithCoordinates(coordinates, size.toULong())
    }
}

@OptIn(ExperimentalForeignApi::class)
private fun MKMapView.focusOn(point: RoutePoint, radiusMeters: Double) {
    setRegion(
        MKCoordinateRegionMakeWithDistance(
            centerCoordinate = CLLocationCoordinate2DMake(point.latitude, point.longitude),
            latitudinalMeters = radiusMeters * 2,
            longitudinalMeters = radiusMeters * 2,
        ),
        animated = true,
    )
}

@OptIn(ExperimentalForeignApi::class)
private fun MKMapView.focusOnCenter(radiusMeters: Double) {
    setRegion(
        MKCoordinateRegionMakeWithDistance(
            centerCoordinate = centerCoordinate,
            latitudinalMeters = radiusMeters * 2,
            longitudinalMeters = radiusMeters * 2,
        ),
        animated = true,
    )
}
