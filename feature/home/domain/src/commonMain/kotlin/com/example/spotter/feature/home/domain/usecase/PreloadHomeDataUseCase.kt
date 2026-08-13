package com.example.spotter.feature.home.domain.usecase

import com.example.spotter.core.domain.result.RestResult
import com.example.spotter.feature.home.domain.model.HomePreloadResult
import com.example.spotter.feature.home.domain.model.SpotSearchQuery
import com.example.spotter.feature.home.domain.repository.HomePreloadRepository
import com.example.spotter.feature.home.domain.repository.HomeRepository
import kotlinx.coroutines.TimeoutCancellationException
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.withTimeout

class PreloadHomeDataUseCase(
    private val resolveSearchLocationUseCase: ResolveSearchLocationUseCase,
    private val homeRepository: HomeRepository,
    private val homePreloadRepository: HomePreloadRepository,
) {
    suspend operator fun invoke(
        onProgress: (Float) -> Unit = {},
    ): HomePreloadResult {
        onProgress(0.15f)
        val searchQuery = resolveSearchLocationWithTimeout()
        onProgress(0.35f)

        val partialResult = HomePreloadResult(
            searchQuery = searchQuery,
            spots = emptyList(),
        )
        homePreloadRepository.save(partialResult)

        val networkResult = try {
            withTimeout(NETWORK_TIMEOUT_MS) {
                homeRepository
                    .getNearbySpots(searchQuery)
                    .first { it !is RestResult.Loading }
            }
        } catch (_: TimeoutCancellationException) {
            null
        }

        onProgress(0.9f)

        val preloadResult = when (networkResult) {
            null -> HomePreloadResult(
                searchQuery = searchQuery,
                spots = emptyList(),
                errorMessage = PRELOAD_TIMEOUT_MESSAGE,
            )

            is RestResult.Success -> HomePreloadResult(
                searchQuery = searchQuery,
                spots = networkResult.result,
            )

            is RestResult.Error -> HomePreloadResult(
                searchQuery = searchQuery,
                spots = networkResult.result.orEmpty(),
                errorMessage = networkResult.error.message,
            )

            is RestResult.Loading -> HomePreloadResult(
                searchQuery = searchQuery,
                spots = emptyList(),
            )
        }

        homePreloadRepository.save(preloadResult)
        onProgress(1f)
        return preloadResult
    }

    fun ensureFallbackSaved() {
        if (homePreloadRepository.peek() != null) return
        homePreloadRepository.save(
            HomePreloadResult(
                searchQuery = SpotSearchQuery.fallback(),
                spots = emptyList(),
            ),
        )
    }

    private suspend fun resolveSearchLocationWithTimeout(): SpotSearchQuery =
        try {
            withTimeout(LOCATION_TIMEOUT_MS) {
                resolveSearchLocationUseCase()
            }
        } catch (_: TimeoutCancellationException) {
            SpotSearchQuery.fallback()
        }

    private companion object {
        const val LOCATION_TIMEOUT_MS = 12_000L
        const val NETWORK_TIMEOUT_MS = 20_000L
        const val PRELOAD_TIMEOUT_MESSAGE = "Nearby spots preload timed out"
    }
}
