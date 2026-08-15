package com.example.spotter.feature.map.contract

import com.example.spotter.core.navigation.NavigationCommand
import kotlinx.serialization.Serializable

@Serializable
data class MapScreenDestination(
    val userLatitude: Double,
    val userLongitude: Double,
    val focusedSpotId: Long? = null,
    val category: String? = null,
) : NavigationCommand.Destination
