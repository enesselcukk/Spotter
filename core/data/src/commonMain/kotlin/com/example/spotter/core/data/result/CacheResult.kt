package com.example.spotter.core.data.result

sealed interface CacheResult<out T> {
    data class Success<T>(val data: T) : CacheResult<T>

    data class Error(val exception: Throwable) : CacheResult<Nothing>
}
