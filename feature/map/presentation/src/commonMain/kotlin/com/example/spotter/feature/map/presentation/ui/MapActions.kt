package com.example.spotter.feature.map.presentation.ui

import com.example.spotter.feature.map.domain.model.TravelMode

internal interface MapActions {
    fun onSpotSelected(spotId: Long)
    fun onTravelModeSelected(travelMode: TravelMode)
    fun onStepsToggle()
    fun onRetryRoute()
    fun onBack()
}
