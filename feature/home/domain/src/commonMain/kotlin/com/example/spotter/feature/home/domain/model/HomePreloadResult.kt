package com.example.spotter.feature.home.domain.model

data class HomePreloadResult(
    val searchQuery: SpotSearchQuery,
    val spots: List<SpotDto>,
    val errorMessage: String? = null,
)
