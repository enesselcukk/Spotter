package com.example.spotter.feature.home.data.repository

import com.example.spotter.feature.home.domain.model.HomePreloadResult
import com.example.spotter.feature.home.domain.repository.HomePreloadRepository

internal class HomePreloadRepositoryImpl : HomePreloadRepository {
    private var cached: HomePreloadResult? = null

    override fun save(result: HomePreloadResult) {
        cached = result
    }

    override fun peek(): HomePreloadResult? = cached

    override fun consume(): HomePreloadResult? = cached.also {
        cached = null
    }
}
