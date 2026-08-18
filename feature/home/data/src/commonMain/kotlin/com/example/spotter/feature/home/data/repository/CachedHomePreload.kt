package com.example.spotter.feature.home.data.repository

import com.example.spotter.feature.home.domain.model.HomePreloadResult
import com.example.spotter.feature.home.domain.model.SpotDto
import com.example.spotter.feature.home.domain.model.SpotSearchQuery
import kotlinx.serialization.Serializable

@Serializable
internal data class CachedHomePreload(
    val searchQuery: SpotSearchQuery,
    val spots: List<SpotDto>,
    val cachedAtEpochMs: Long,
) {
    fun isFresh(nowEpochMs: Long = currentEpochMs()): Boolean =
        nowEpochMs - cachedAtEpochMs <= CACHE_TTL_MS

    fun toResult(): HomePreloadResult = HomePreloadResult(
        searchQuery = searchQuery,
        spots = spots,
    )

    companion object {
        const val CACHE_TTL_MS = 30L * 60L * 1_000L

        fun fromResult(result: HomePreloadResult): CachedHomePreload = CachedHomePreload(
            searchQuery = result.searchQuery,
            spots = result.spots,
            cachedAtEpochMs = currentEpochMs(),
        )
    }
}

internal expect fun currentEpochMs(): Long
