package com.example.spotter.core.database.model

import androidx.room3.Entity
import androidx.room3.PrimaryKey
import com.example.spotter.core.model.Article
import com.example.spotter.core.model.Source

@Entity(tableName = "articles")
data class ArticleEntity(
    @PrimaryKey
    val id: String,
    val title: String,
    val description: String?,
    val content: String?,
    val url: String,
    val imageUrl: String?,
    val publishedAt: String,
    val sourceId: String,
    val sourceName: String,
    val author: String?,
) {
    fun toDomain(): Article = Article(
        id = id,
        title = title,
        description = description,
        content = content,
        url = url,
        imageUrl = imageUrl,
        publishedAt = publishedAt,
        source = Source(id = sourceId, name = sourceName),
        author = author,
    )

    companion object {
        fun fromDomain(article: Article): ArticleEntity = ArticleEntity(
            id = article.id,
            title = article.title,
            description = article.description,
            content = article.content,
            url = article.url,
            imageUrl = article.imageUrl,
            publishedAt = article.publishedAt,
            sourceId = article.source.id,
            sourceName = article.source.name,
            author = article.author,
        )
    }
}
