package com.example.spotter.feature.map.presentation.map

import androidx.compose.runtime.Composable
import androidx.compose.runtime.Stable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import com.example.spotter.feature.home.domain.model.SpotDto
import com.example.spotter.feature.map.domain.model.RoutePoint

@Composable
expect fun RouteMapView(
    userLocation: RoutePoint,
    spots: List<SpotDto>,
    selectedSpotId: Long?,
    routeGeometry: List<RoutePoint>,
    mapState: RouteMapState,
    onSpotSelected: (Long) -> Unit,
    modifier: Modifier = Modifier,
)

enum class MapCamera {
    FollowUser,
    FitRoute,
    ZoomIn,
    ZoomOut,
}

/**
 * Camera commands are carried as one-shot tokens so the platform map can distinguish two
 * identical requests in a row (two consecutive zoom-in taps, for example).
 */
data class MapCameraCommand(
    val token: Long,
    val camera: MapCamera,
)

@Stable
class RouteMapState {
    var pendingCommand: MapCameraCommand? by mutableStateOf(null)
        private set

    private var token = 0L

    fun send(camera: MapCamera) {
        token += 1
        pendingCommand = MapCameraCommand(token, camera)
    }

    fun consume() {
        pendingCommand = null
    }
}

@Composable
fun rememberRouteMapState(): RouteMapState = remember { RouteMapState() }
