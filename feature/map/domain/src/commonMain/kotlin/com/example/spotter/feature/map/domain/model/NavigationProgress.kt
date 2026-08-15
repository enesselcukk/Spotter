package com.example.spotter.feature.map.domain.model

data class NavigationProgress(
    val currentStepIndex: Int,
    val currentStep: RouteStep,
    val distanceToManeuverMeters: Double,
    val remainingDistanceMeters: Double,
    val remainingDurationSeconds: Double,
    val isArrived: Boolean,
)
