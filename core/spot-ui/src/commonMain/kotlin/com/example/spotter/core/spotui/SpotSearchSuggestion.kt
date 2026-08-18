package com.example.spotter.core.spotui

import androidx.compose.runtime.Immutable
import com.example.spotter.feature.home.domain.model.SpotDto

@Immutable
data class SpotSearchSuggestion(
    val id: String,
    val label: String,
    val category: String?,
    val spotId: Long? = null,
    val kind: SpotSearchSuggestionKind,
)

enum class SpotSearchSuggestionKind {
    Recent,
    Spot,
    Category,
}

fun buildSearchSuggestions(
    spots: List<SpotDto>,
    query: String,
    recentQueries: List<String>,
    categories: List<String>,
): List<SpotSearchSuggestion> {
    if (query.isBlank()) {
        val recent = recentQueries.map { label ->
            SpotSearchSuggestion(
                id = "recent:$label",
                label = label,
                category = null,
                kind = SpotSearchSuggestionKind.Recent,
            )
        }
        val picks = spots
            .filter { !it.name.isNullOrBlank() }
            .distinctBy { it.name }
            .take(4)
            .map { spot ->
                SpotSearchSuggestion(
                    id = "spot:${spot.id}",
                    label = spot.name.orEmpty(),
                    category = spot.amenity,
                    spotId = spot.id,
                    kind = SpotSearchSuggestionKind.Spot,
                )
            }
        return (recent + picks).distinctBy { it.label }.take(6)
    }

    val spotMatches = spots
        .filter { it.matchesSearch(query) }
        .distinctBy { it.id }
        .take(6)
        .map { spot ->
            SpotSearchSuggestion(
                id = "spot:${spot.id}",
                label = spot.name ?: spot.socketSummary ?: spot.amenity.orEmpty(),
                category = spot.amenity,
                spotId = spot.id,
                kind = SpotSearchSuggestionKind.Spot,
            )
        }

    if (spotMatches.isNotEmpty()) return spotMatches

    val normalized = query.trim().lowercase()
    return categories
        .filter { category -> category.contains(normalized, ignoreCase = true) }
        .take(3)
        .map { category ->
            SpotSearchSuggestion(
                id = "category:$category",
                label = query.trim(),
                category = category,
                kind = SpotSearchSuggestionKind.Category,
            )
        }
}
