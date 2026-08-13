package com.example.spotter.feature.home.presentation.map

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
import org.osmdroid.util.GeoPoint
import org.osmdroid.views.CustomZoomButtonsController
import org.osmdroid.views.MapView
import org.osmdroid.views.overlay.Marker
import org.osmdroid.tileprovider.tilesource.XYTileSource

private val MapBackgroundColor = Color.parseColor("#1A2332")

@Composable
actual fun SpotMapView(
    spots: List<SpotDto>,
    selectedSpotId: Long?,
    userLatitude: Double,
    userLongitude: Double,
    onSpotSelected: (Long) -> Unit,
    modifier: Modifier,
) {
    val context = LocalContext.current
    val lifecycleOwner = LocalLifecycleOwner.current
    var isMapReady by remember { mutableStateOf(false) }
    val mapBackground = MaterialTheme.colorScheme.background

    val mapView = remember {
        MapView(context).apply {
            layoutParams = ViewGroup.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.MATCH_PARENT,
            )
            setBackgroundColor(MapBackgroundColor)
            setMultiTouchControls(true)
            zoomController.setVisibility(CustomZoomButtonsController.Visibility.NEVER)
            controller.setZoom(14.0)
            setTileSource(DarkMapTileSource)
            isHorizontalMapRepetitionEnabled = false
            isVerticalMapRepetitionEnabled = false
            isTilesScaledToDpi = true

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
        mapView.onResume()
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

    LaunchedEffect(spots, selectedSpotId, userLatitude, userLongitude) {
        mapView.overlays.clear()

        val userMarker = Marker(mapView).apply {
            position = GeoPoint(userLatitude, userLongitude)
            title = "Me"
            setAnchor(Marker.ANCHOR_CENTER, Marker.ANCHOR_CENTER)
            icon = createUserMarker(context)
        }
        mapView.overlays.add(userMarker)

        spots.forEachIndexed { index, spot ->
            val isSelected = spot.id == selectedSpotId
            val marker = Marker(mapView).apply {
                position = GeoPoint(spot.lat, spot.lon)
                title = spot.name
                setAnchor(Marker.ANCHOR_CENTER, Marker.ANCHOR_BOTTOM)
                icon = createNumberedMarker(context, index + 1, isSelected)
                setOnMarkerClickListener { clickedMarker, _ ->
                    onSpotSelected(spot.id)
                    clickedMarker.icon = createNumberedMarker(context, index + 1, selected = true)
                    mapView.invalidate()
                    true
                }
            }
            mapView.overlays.add(marker)
        }

        val center = selectedSpotId?.let { id ->
            spots.find { it.id == id }?.let { GeoPoint(it.lat, it.lon) }
        } ?: GeoPoint(userLatitude, userLongitude)

        mapView.controller.setCenter(center)
        mapView.invalidate()
    }

    Box(modifier = modifier.background(mapBackground)) {
        AndroidView(
            modifier = Modifier.fillMaxSize(),
            factory = { mapView },
            update = { view ->
                view.setBackgroundColor(MapBackgroundColor)
                view.onResume()
                view.invalidate()
            },
        )

        if (!isMapReady) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(mapBackground),
                contentAlignment = Alignment.Center,
            ) {
                CircularProgressIndicator(
                    color = MaterialTheme.colorScheme.primary,
                )
            }
        }
    }
}

private fun createUserMarker(context: android.content.Context): BitmapDrawable {
    val size = 48
    val bitmap = android.graphics.Bitmap.createBitmap(size, size, android.graphics.Bitmap.Config.ARGB_8888)
    val canvas = Canvas(bitmap)
    val outerPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
        color = Color.parseColor("#332196F3")
        style = Paint.Style.FILL
    }
    val innerPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
        color = Color.parseColor("#2196F3")
        style = Paint.Style.FILL
    }
    val radius = size / 2f
    canvas.drawCircle(radius, radius, radius, outerPaint)
    canvas.drawCircle(radius, radius, radius * 0.45f, innerPaint)
    return BitmapDrawable(context.resources, bitmap)
}

private fun createNumberedMarker(
    context: android.content.Context,
    number: Int,
    selected: Boolean,
): BitmapDrawable {
    val size = if (selected) 72 else 60
    val bitmap = android.graphics.Bitmap.createBitmap(size, size, android.graphics.Bitmap.Config.ARGB_8888)
    val canvas = Canvas(bitmap)

    val fillPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
        color = if (selected) Color.parseColor("#FFC107") else Color.parseColor("#FFB300")
        style = Paint.Style.FILL
    }
    val strokePaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
        color = Color.parseColor("#1A2332")
        style = Paint.Style.STROKE
        strokeWidth = 4f
    }
    val textPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
        color = Color.parseColor("#1A2332")
        textAlign = Paint.Align.CENTER
        textSize = if (selected) 28f else 24f
        isFakeBoldText = true
    }

    val radius = size / 2f
    canvas.drawCircle(radius, radius, radius - 2f, fillPaint)
    canvas.drawCircle(radius, radius, radius - 2f, strokePaint)
    canvas.drawText(number.toString(), radius, radius + 10f, textPaint)

    return BitmapDrawable(context.resources, bitmap)
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
