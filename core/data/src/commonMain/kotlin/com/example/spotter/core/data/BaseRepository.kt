package com.example.spotter.core.data

import com.example.spotter.core.data.dispatcher.repositoryIoDispatcher
import com.example.spotter.core.data.exception.shouldRetryNetworkCall
import com.example.spotter.core.data.extension.asRestResult
import com.example.spotter.core.data.result.CacheResult
import com.example.spotter.core.domain.result.RestResult
import io.ktor.client.statement.HttpResponse
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.withContext

abstract class BaseRepository {

    @PublishedApi
    internal suspend fun <T> withIoContext(block: suspend () -> T): T =
        withContext(repositoryIoDispatcher) { block() }

    protected suspend inline fun <reified T : Any> safeApiCall(
        crossinline call: suspend () -> HttpResponse,
    ): RestResult<T> = withIoContext {
        try {
            call().asRestResult()
        } catch (e: Exception) {
            RestResult.Error(e)
        }
    }

    protected suspend inline fun <reified T : Any> safeApiCallWithRetry(
        maxAttempts: Int = 3,
        initialDelayMs: Long = 1_500L,
        crossinline call: suspend () -> HttpResponse,
    ): RestResult<T> = withIoContext {
        var delayMs = initialDelayMs
        var lastError: Throwable? = null

        repeat(maxAttempts) { attempt ->
            when (val result = safeApiCall<T> { call() }) {
                is RestResult.Success -> return@withIoContext result
                is RestResult.Error -> {
                    lastError = result.error
                    val canRetry = result.error.shouldRetryNetworkCall() && attempt < maxAttempts - 1
                    if (!canRetry) return@withIoContext result
                }
                is RestResult.Loading -> Unit
            }
            kotlinx.coroutines.delay(delayMs)
            delayMs = (delayMs * 1.5).toLong().coerceAtMost(6_000L)
        }

        RestResult.Error(lastError ?: IllegalStateException("Network request failed"))
    }

    protected suspend inline fun <reified T> safeCacheCall(
        crossinline call: suspend () -> T,
    ): CacheResult<T> = withIoContext {
        try {
            CacheResult.Success(call())
        } catch (e: Exception) {
            CacheResult.Error(e)
        }
    }

    protected inline fun <reified NetworkType : Any, reified LocalType, DomainType> offlineFirstFlow(
        crossinline fetchFromNetwork: suspend () -> HttpResponse,
        crossinline saveToLocal: suspend (NetworkType) -> Unit,
        crossinline readFromLocal: suspend () -> LocalType,
        crossinline mapToDomain: (LocalType) -> DomainType,
        crossinline mapNetworkToDomain: (NetworkType) -> DomainType,
        crossinline shouldFetch: (LocalType) -> Boolean = { true },
    ): Flow<RestResult<DomainType>> = flow {
        emit(RestResult.Loading())

        val cacheResponse = safeCacheCall { readFromLocal() }
        val cachedData = (cacheResponse as? CacheResult.Success)?.data

        val hasCachedData = cachedData != null &&
            (cachedData !is List<*> || cachedData.isNotEmpty())

        if (hasCachedData) {
            emit(RestResult.Loading(result = mapToDomain(cachedData as LocalType)))
        }

        if (cachedData != null && !shouldFetch(cachedData)) {
            emit(RestResult.Success(mapToDomain(cachedData)))
            return@flow
        }

        val networkResponse = safeApiCall<NetworkType> { fetchFromNetwork() }

        when (networkResponse) {
            is RestResult.Success -> {
                safeCacheCall<Unit> { saveToLocal(networkResponse.result) }

                val freshCacheResponse = safeCacheCall { readFromLocal() }
                val freshData = (freshCacheResponse as? CacheResult.Success)?.data

                if (freshData != null && (freshData !is List<*> || freshData.isNotEmpty())) {
                    emit(RestResult.Success(mapToDomain(freshData)))
                } else {
                    emit(RestResult.Success(mapNetworkToDomain(networkResponse.result)))
                }
            }

            is RestResult.Loading -> {
                emit(RestResult.Loading())
            }

            is RestResult.Error -> {
                val currentData = if (hasCachedData) {
                    mapToDomain(cachedData as LocalType)
                } else {
                    null
                }

                emit(RestResult.Error(networkResponse.error, result = currentData))
            }
        }
    }

    protected inline fun <reified NetworkType : Any, DomainType> networkOnlyFlow(
        crossinline fetchFromNetwork: suspend () -> HttpResponse,
        crossinline mapToDomain: (NetworkType) -> DomainType,
    ): Flow<RestResult<DomainType>> = flow {
        emit(RestResult.Loading())

        val networkResponse = safeApiCallWithRetry<NetworkType> { fetchFromNetwork() }

        when (networkResponse) {
            is RestResult.Success -> {
                emit(RestResult.Success(mapToDomain(networkResponse.result)))
            }

            is RestResult.Error -> {
                emit(RestResult.Error(networkResponse.error))
            }

            is RestResult.Loading -> {
                emit(RestResult.Loading())
            }
        }
    }
}
