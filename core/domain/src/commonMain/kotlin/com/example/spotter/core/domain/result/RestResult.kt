package com.example.spotter.core.domain.result

sealed interface RestResult<out T> {
    data class Loading<T>(val result: T? = null) : RestResult<T>

    data class Success<T>(val result: T) : RestResult<T>

    data class Error<T>(
        val error: Throwable,
        val result: T? = null,
    ) : RestResult<T>
}
