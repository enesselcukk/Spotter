package com.example.spotter.feature.home.data.repository

import com.example.spotter.feature.home.domain.model.HomePreloadResult
import com.example.spotter.feature.home.domain.repository.HomePreloadRepository
import com.russhwolf.settings.Settings
import com.russhwolf.settings.set
import kotlinx.serialization.json.Json

internal class HomePreloadRepositoryImpl(
    private val settings: Settings = Settings(),
    private val json: Json = Json { ignoreUnknownKeys = true },
) : HomePreloadRepository {
    private var memoryCache: HomePreloadResult? = null

    override fun save(result: HomePreloadResult) {
        memoryCache = result
        if (result.spots.isNotEmpty() && result.errorMessage == null) {
            persist(result)
        }
    }

    override fun peek(): HomePreloadResult? = memoryCache ?: getFreshCache()

    override fun consume(): HomePreloadResult? {
        val memory = memoryCache
        memoryCache = null

        return when {
            memory?.spots?.isNotEmpty() == true -> memory
            else -> getFreshCache() ?: getAnyCache() ?: memory
        }
    }

    override fun getFreshCache(): HomePreloadResult? = readPersistent(requireFresh = true)

    override fun getAnyCache(): HomePreloadResult? = readPersistent(requireFresh = false)

    private fun persist(result: HomePreloadResult) {
        settings[CACHE_KEY] = json.encodeToString(
            CachedHomePreload.serializer(),
            CachedHomePreload.fromResult(result),
        )
    }

    private fun readPersistent(requireFresh: Boolean): HomePreloadResult? {
        val stored = settings.getStringOrNull(CACHE_KEY) ?: return null
        val cached = runCatching {
            json.decodeFromString(CachedHomePreload.serializer(), stored)
        }.getOrNull() ?: return null
        if (requireFresh && !cached.isFresh()) return null
        return cached.toResult()
    }

    private companion object {
        const val CACHE_KEY = "home_preload_cache"
    }
}
