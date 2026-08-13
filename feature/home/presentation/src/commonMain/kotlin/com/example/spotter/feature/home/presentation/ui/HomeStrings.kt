package com.example.spotter.feature.home.presentation.ui

import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.spotter.core.datastore.DistanceUnit
import com.example.spotter.core.datastore.UserSettingsRepository
import com.example.spotter.feature.home.domain.model.SpotDto
import com.example.spotter.feature.home.domain.util.GeoDistance
import com.example.spotter.feature.home.presentation.generated.resources.Res
import com.example.spotter.feature.home.presentation.generated.resources.*
import org.jetbrains.compose.resources.stringResource
import org.koin.compose.koinInject

@Composable
fun homeCategoryLabel(categoryId: String): String = when (categoryId) {
    HomeCategories.CHARGING -> stringResource(Res.string.category_charging)
    HomeCategories.CAR_WASH -> stringResource(Res.string.category_car_wash)
    HomeCategories.PARKING -> stringResource(Res.string.category_parking)
    HomeCategories.FUEL -> stringResource(Res.string.category_fuel)
    HomeCategories.CAR_REPAIR -> stringResource(Res.string.category_car_repair)
    else -> categoryId
}

@Composable
fun spotDisplayName(spot: SpotDto): String =
    spot.name ?: stringResource(Res.string.home_unnamed_spot)

@Composable
fun spotDistanceLabel(spot: SpotDto): String {
    val settingsRepository: UserSettingsRepository = koinInject()
    val distanceUnit by settingsRepository.distanceUnit.collectAsStateWithLifecycle(DistanceUnit.KILOMETERS)
    return spot.distanceMeters?.let { GeoDistance.formatDistance(it, distanceUnit) }
        ?: stringResource(Res.string.home_unknown_distance)
}

@Composable
fun spotOpeningHoursLabel(spot: SpotDto): String =
    when {
        spot.openingHours.equals("24/7", ignoreCase = true) ->
            stringResource(Res.string.home_open_24h)
        !spot.openingHours.isNullOrBlank() -> spot.openingHours.orEmpty()
        else -> stringResource(Res.string.home_hours_unknown)
    }

@Composable
fun spotOperatorLabel(spot: SpotDto): String? =
    spot.operator?.let { stringResource(Res.string.home_operator, it) }

@Composable
fun resolveLocationLabel(
    locationLabel: String?,
    usesDeviceLocation: Boolean,
): String = when {
    !locationLabel.isNullOrBlank() -> locationLabel
    locationLabel == null -> stringResource(Res.string.home_location_loading)
    usesDeviceLocation -> stringResource(Res.string.home_location_current)
    else -> stringResource(Res.string.home_location_fallback)
}

@Composable
fun spotSocketLabel(spot: SpotDto): String? =
    spot.socketSummary ?: when (spot.amenity) {
        HomeCategories.CHARGING -> stringResource(Res.string.home_charging_default_socket)
        HomeCategories.FUEL -> stringResource(Res.string.home_fuel_default_detail)
        HomeCategories.PARKING -> stringResource(Res.string.home_parking_default_detail)
        HomeCategories.CAR_WASH -> stringResource(Res.string.home_car_wash_default_detail)
        HomeCategories.CAR_REPAIR -> stringResource(Res.string.home_car_repair_default_detail)
        else -> null
    }
