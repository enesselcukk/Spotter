package com.example.spotter.core.data.exception

private val RETRYABLE_STATUS_CODES = setOf(408, 429, 500, 502, 503, 504, 524)

fun Throwable.shouldRetryNetworkCall(): Boolean = when (this) {
    is HttpStatusException -> statusCode in RETRYABLE_STATUS_CODES
    else -> {
        val message = message.orEmpty()
        message.contains("timeout", ignoreCase = true) ||
            message.contains("timed out", ignoreCase = true) ||
            message.contains("connection", ignoreCase = true)
    }
}
