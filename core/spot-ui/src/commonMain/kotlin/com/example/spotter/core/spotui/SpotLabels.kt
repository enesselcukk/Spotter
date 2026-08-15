package com.example.spotter.core.spotui

import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.spotter.core.datastore.DistanceUnit
import com.example.spotter.core.datastore.UserSettingsRepository
import com.example.spotter.core.spotui.generated.resources.Res
import com.example.spotter.core.spotui.generated.resources.spot_car_repair_default_detail
import com.example.spotter.core.spotui.generated.resources.spot_car_wash_default_detail
import com.example.spotter.core.spotui.generated.resources.spot_category_car_repair
import com.example.spotter.core.spotui.generated.resources.spot_category_car_wash
import com.example.spotter.core.spotui.generated.resources.spot_category_charging
import com.example.spotter.core.spotui.generated.resources.spot_category_fuel
import com.example.spotter.core.spotui.generated.resources.spot_category_parking
import com.example.spotter.core.spotui.generated.resources.spot_charging_default_socket
import com.example.spotter.core.spotui.generated.resources.spot_fuel_default_detail
import com.example.spotter.core.spotui.generated.resources.spot_hours_unknown
import com.example.spotter.core.spotui.generated.resources.spot_open_24h
import com.example.spotter.core.spotui.generated.resources.spot_operator
import com.example.spotter.core.spotui.generated.resources.spot_parking_default_detail
import com.example.spotter.core.spotui.generated.resources.spot_unknown_distance
import com.example.spotter.core.spotui.generated.resources.spot_unnamed
import com.example.spotter.feature.home.domain.model.SpotDto
import com.example.spotter.feature.home.domain.util.GeoDistance
import org.jetbrains.compose.resources.stringResource
import org.koin.compose.koinInject

@Composable
fun spotCategoryLabel(categoryId: String): String = when (categoryId) {
    SpotCategories.CHARGING -> stringResource(Res.string.spot_category_charging)
    SpotCategories.CAR_WASH -> stringResource(Res.string.spot_category_car_wash)
    SpotCategories.PARKING -> stringResource(Res.string.spot_category_parking)
    SpotCategories.FUEL -> stringResource(Res.string.spot_category_fuel)
    SpotCategories.CAR_REPAIR -> stringResource(Res.string.spot_category_car_repair)
    else -> categoryId
}

@Composable
fun spotDisplayName(spot: SpotDto): String =
    spot.name ?: stringResource(Res.string.spot_unnamed)

@Composable
fun spotDistanceLabel(spot: SpotDto): String {
    val settingsRepository: UserSettingsRepository = koinInject()
    val distanceUnit by settingsRepository.distanceUnit.collectAsStateWithLifecycle(DistanceUnit.KILOMETERS)
    return spot.distanceMeters?.let { GeoDistance.formatDistance(it, distanceUnit) }
        ?: stringResource(Res.string.spot_unknown_distance)
}

@Composable
fun spotOpeningHoursLabel(spot: SpotDto): String = when {
    spot.openingHours.equals("24/7", ignoreCase = true) -> stringResource(Res.string.spot_open_24h)
    !spot.openingHours.isNullOrBlank() -> spot.openingHours.orEmpty()
    else -> stringResource(Res.string.spot_hours_unknown)
}

@Composable
fun spotOperatorLabel(spot: SpotDto): String? =
    spot.operator?.let { stringResource(Res.string.spot_operator, it) }

@Composable
fun spotSocketLabel(spot: SpotDto): String? =
    spot.socketSummary ?: when (spot.amenity) {
        SpotCategories.CHARGING -> stringResource(Res.string.spot_charging_default_socket)
        SpotCategories.FUEL -> stringResource(Res.string.spot_fuel_default_detail)
        SpotCategories.PARKING -> stringResource(Res.string.spot_parking_default_detail)
        SpotCategories.CAR_WASH -> stringResource(Res.string.spot_car_wash_default_detail)
        SpotCategories.CAR_REPAIR -> stringResource(Res.string.spot_car_repair_default_detail)
        else -> null
    }
