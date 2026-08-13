package com.example.spotter.core.model

import kotlinx.serialization.Serializable

@Serializable
data class Article(
    val id: String,
    val title: String,
    val description: String?,
    val content: String?,
    val url: String,
    val imageUrl: String?,
    val publishedAt: String,
    val source: Source,
    val author: String?,
)
