package com.example.spotter.feature.home.domain.repository

import com.example.spotter.feature.home.domain.model.HomePreloadResult

interface HomePreloadRepository {
    fun save(result: HomePreloadResult)
    fun peek(): HomePreloadResult?
    fun consume(): HomePreloadResult?
}
