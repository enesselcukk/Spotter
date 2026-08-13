package com.example.spotter.core.model

import kotlin.test.Test
import kotlin.test.assertEquals
import kotlinx.serialization.json.Json

class ArticleTest {
  private val json = Json { ignoreUnknownKeys = true }

  @Test
  fun article_roundTripSerialization() {
    val article = Article(
      id = "1",
      title = "Kotlin Multiplatform",
      description = "Cross-platform development",
      content = null,
      url = "https://kotlinlang.org",
      imageUrl = null,
      publishedAt = "2026-08-08T09:00:00Z",
      source = Source(id = "kotlin", name = "Kotlin Blog"),
      author = "JetBrains",
    )

    val encoded = json.encodeToString(Article.serializer(), article)
    val decoded = json.decodeFromString(Article.serializer(), encoded)

    assertEquals(article, decoded)
  }
}
