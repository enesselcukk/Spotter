package com.example.spotter.feature.map.presentation.map

import android.content.Context
import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.graphics.drawable.BitmapDrawable
import android.view.MotionEvent
import android.view.ViewGroup
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.viewinterop.AndroidView
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver
import androidx.lifecycle.compose.LocalLifecycleOwner
import com.example.spotter.feature.home.domain.model.SpotDto
import com.example.spotter.feature.map.domain.model.RoutePoint
import org.osmdroid.tileprovider.tilesource.XYTileSource
import org.osmdroid.util.BoundingBox
import org.osmdroid.util.GeoPoint
import org.osmdroid.views.CustomZoomButtonsController
import org.osmdroid.views.MapView
import org.osmdroid.views.overlay.Marker
import org.osmdroid.views.overlay.Polyline

private const val MapBackgroundHex = "#0E1116"
private const val RouteCasingHex = "#0B3D6B"
private const val RouteStrokeHex = "#4285F4"
private const val DefaultZoom = 15.0
private const val RoutePaddingPx = 140

@Composable
actual fun RouteMapView(
    userLocation: RoutePoint,
    spots: List<SpotDto>,
    selectedSpotId: Long?,
    routeGeometry: List<RoutePoint>,
    mapState: RouteMapState,
    onSpotSelected: (Long) -> Unit,
    modifier: Modifier,
) {
    val context = LocalContext.current
    val lifecycleOwner = LocalLifecycleOwner.current
    var isMapReady by remember { mutableStateOf(false) }
    val backgroundColor = MaterialTheme.colorScheme.background

    val mapView = remember {
        MapView(context).apply {
            layoutParams = ViewGroup.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.MATCH_PARENT,
            )
            setBackgroundColor(Color.parseColor(MapBackgroundHex))
            setMultiTouchControls(true)
            zoomController.setVisibility(CustomZoomButtonsController.Visibility.NEVER)
            setTileSource(DarkMapTileSource)
            isHorizontalMapRepetitionEnabled = false
            isVerticalMapRepetitionEnabled = false
            isTilesScaledToDpi = true
            controller.setZoom(DefaultZoom)
            controller.setCenter(GeoPoint(userLocation.latitude, userLocation.longitude))

            setOnTouchListener { view, event ->
                when (event.actionMasked) {
                    MotionEvent.ACTION_DOWN, MotionEvent.ACTION_MOVE ->
                        view.parent?.requestDisallowInterceptTouchEvent(true)
                }
                false
            }

            onResume()
        }
    }

    DisposableEffect(lifecycleOwner, mapView) {
        val observer = LifecycleEventObserver { _, event ->
            when (event) {
                Lifecycle.Event.ON_RESUME -> mapView.onResume()
                Lifecycle.Event.ON_PAUSE -> mapView.onPause()
                else -> Unit
            }
        }
        lifecycleOwner.lifecycle.addObserver(observer)
        onDispose {
            lifecycleOwner.lifecycle.removeObserver(observer)
            mapView.onPause()
        }
    }

    LaunchedEffect(mapView) {
        mapView.post {
            mapView.onResume()
            mapView.invalidate()
            isMapReady = true
        }
    }

    LaunchedEffect(mapView, spots, selectedSpotId, userLocation, routeGeometry) {
        mapView.overlays.clear()

        if (routeGeometry.size > 1) {
            mapView.overlays.add(routePolyline(routeGeometry, RouteCasingHex, 22f))
            mapView.overlays.add(routePolyline(routeGeometry, RouteStrokeHex, 13f))
        }

        spots.forEach { spot ->
            val isSelected = spot.id == selectedSpotId
            mapView.overlays.add(
                Marker(mapView).apply {
                    position = GeoPoint(spot.lat, spot.lon)
                    title = spot.name
                    setAnchor(Marker.ANCHOR_CENTER, if (isSelected) Marker.ANCHOR_BOTTOM else Marker.ANCHOR_CENTER)
                    icon = if (isSelected) destinationPin(context) else spotDot(context)
                    setOnMarkerClickListener { _, _ ->
                        onSpotSelected(spot.id)
                        true
                    }
                },
            )
        }

        mapView.overlays.add(
            Marker(mapView).apply {
                position = GeoPoint(userLocation.latitude, userLocation.longitude)
                setAnchor(Marker.ANCHOR_CENTER, Marker.ANCHOR_CENTER)
                icon = userDot(context)
                setInfoWindow(null)
            },
        )

        mapView.post {
            if (routeGeometry.size > 1) {
                mapView.zoomToBoundingBox(routeGeometry.toBoundingBox(), true, RoutePaddingPx)
            } else {
                mapView.controller.animateTo(GeoPoint(userLocation.latitude, userLocation.longitude))
            }
        }
        mapView.invalidate()
    }

    LaunchedEffect(mapState.pendingCommand) {
        val command = mapState.pendingCommand ?: return@LaunchedEffect
        when (command.camera) {
            MapCamera.FollowUser -> {
                mapView.controller.animateTo(GeoPoint(userLocation.latitude, userLocation.longitude))
                mapView.controller.setZoom(DefaultZoom)
            }

            MapCamera.FitRoute -> {
                if (routeGeometry.size > 1) {
                    mapView.zoomToBoundingBox(routeGeometry.toBoundingBox(), true, RoutePaddingPx)
                }
            }

            MapCamera.ZoomIn -> mapView.controller.zoomIn()
            MapCamera.ZoomOut -> mapView.controller.zoomOut()
        }
        mapView.invalidate()
        mapState.consume()
    }

    Box(modifier = modifier.background(backgroundColor)) {
        AndroidView(
            modifier = Modifier.fillMaxSize(),
            factory = { mapView },
            update = { view ->
                view.onResume()
                view.invalidate()
            },
        )

        if (!isMapReady) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(backgroundColor),
                contentAlignment = Alignment.Center,
            ) {
                CircularProgressIndicator(color = MaterialTheme.colorScheme.primary)
            }
        }
    }
}

private fun routePolyline(points: List<RoutePoint>, colorHex: String, width: Float): Polyline =
    Polyline().apply {
        setPoints(points.map { GeoPoint(it.latitude, it.longitude) })
        outlinePaint.color = Color.parseColor(colorHex)
        outlinePaint.strokeWidth = width
        outlinePaint.isAntiAlias = true
        outlinePaint.strokeCap = Paint.Cap.ROUND
        outlinePaint.strokeJoin = Paint.Join.ROUND
        setInfoWindow(null)
    }

private fun List<RoutePoint>.toBoundingBox(): BoundingBox = BoundingBox(
    maxOf { it.latitude },
    maxOf { it.longitude },
    minOf { it.latitude },
    minOf { it.longitude },
)

private fun userDot(context: Context): BitmapDrawable {
    val size = 56
    val bitmap = Bitmap.createBitmap(size, size, Bitmap.Config.ARGB_8888)
    val canvas = Canvas(bitmap)
    val radius = size / 2f

    canvas.drawCircle(radius, radius, radius, fillPaint("#332196F3"))
    canvas.drawCircle(radius, radius, radius * 0.42f, fillPaint("#FFFFFF"))
    canvas.drawCircle(radius, radius, radius * 0.32f, fillPaint("#2196F3"))
    return BitmapDrawable(context.resources, bitmap)
}

private fun spotDot(context: Context): BitmapDrawable {
    val size = 34
    val bitmap = Bitmap.createBitmap(size, size, Bitmap.Config.ARGB_8888)
    val canvas = Canvas(bitmap)
    val radius = size / 2f

    canvas.drawCircle(radius, radius, radius - 2f, fillPaint("#FFB300"))
    canvas.drawCircle(radius, radius, radius - 2f, strokePaint("#0E1116", 3f))
    return BitmapDrawable(context.resources, bitmap)
}

private fun destinationPin(context: Context): BitmapDrawable {
    val width = 60
    val height = 82
    val bitmap = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888)
    val canvas = Canvas(bitmap)
    val centerX = width / 2f
    val headRadius = width / 2f - 3f

    val tail = android.graphics.Path().apply {
        moveTo(centerX - headRadius * 0.55f, headRadius + 6f)
        lineTo(centerX, height - 2f)
        lineTo(centerX + headRadius * 0.55f, headRadius + 6f)
        close()
    }
    canvas.drawPath(tail, fillPaint("#E53935"))
    canvas.drawCircle(centerX, headRadius + 3f, headRadius, fillPaint("#E53935"))
    canvas.drawCircle(centerX, headRadius + 3f, headRadius, strokePaint("#FFFFFF", 4f))
    canvas.drawCircle(centerX, headRadius + 3f, headRadius * 0.36f, fillPaint("#FFFFFF"))

    return BitmapDrawable(context.resources, bitmap)
}

private fun fillPaint(colorHex: String) = Paint(Paint.ANTI_ALIAS_FLAG).apply {
    color = Color.parseColor(colorHex)
    style = Paint.Style.FILL
}

private fun strokePaint(colorHex: String, width: Float) = Paint(Paint.ANTI_ALIAS_FLAG).apply {
    color = Color.parseColor(colorHex)
    style = Paint.Style.STROKE
    strokeWidth = width
}

private val DarkMapTileSource = XYTileSource(
    "CartoDB.DarkMatter",
    0,
    19,
    256,
    ".png",
    arrayOf(
        "https://a.basemaps.cartocdn.com/dark_all/",
        "https://b.basemaps.cartocdn.com/dark_all/",
        "https://c.basemaps.cartocdn.com/dark_all/",
    ),
    "© OpenStreetMap contributors © CARTO",
)
