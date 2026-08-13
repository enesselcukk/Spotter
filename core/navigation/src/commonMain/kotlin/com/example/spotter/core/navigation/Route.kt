package com.example.spotter.core.navigation

import kotlinx.serialization.Serializable

@Serializable
data object NewsListRoute

@Serializable
data class NewsDetailRoute(
    val id: String,
)
