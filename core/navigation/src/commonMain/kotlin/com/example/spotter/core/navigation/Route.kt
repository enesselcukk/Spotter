package com.example.spotter.core.navigation

import androidx.navigation3.runtime.NavKey
import kotlinx.serialization.Serializable

@Serializable
data object NewsListRoute : NavKey

@Serializable
data class NewsDetailRoute(
    val id: String,
) : NavKey
