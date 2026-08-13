package com.example.spotter.core.network.client

import com.example.spotter.core.network.config.OverpassApiConfig.BASE_PATH
import com.example.spotter.core.network.config.OverpassApiConfig.HOST
import com.example.spotter.core.network.engine.getProvide
import io.ktor.client.HttpClient
import io.ktor.client.plugins.HttpTimeout
import io.ktor.client.plugins.contentnegotiation.ContentNegotiation
import io.ktor.client.plugins.defaultRequest
import io.ktor.client.plugins.logging.LogLevel
import io.ktor.client.plugins.logging.Logger
import io.ktor.client.plugins.logging.Logging
import io.ktor.http.URLProtocol
import io.ktor.http.encodedPath
import io.ktor.serialization.kotlinx.json.json
import kotlinx.serialization.json.Json

private const val TIMEOUT_MILLIS = 60_000L

object OverpassApiHttpClientFactory {

    fun create(
        enableLogging: Boolean = true,
    ): HttpClient {
        return HttpClient(getProvide()) {
            install(ContentNegotiation) {
                json(
                    Json {
                        prettyPrint = true
                        ignoreUnknownKeys = true
                        isLenient = true
                    },
                )
            }

            if (enableLogging) {
                install(Logging) {
                    level = LogLevel.INFO
                    logger = object : Logger {
                        override fun log(message: String) {
                            println("[OverpassApiClient]: $message")
                        }
                    }
                }
            }

            install(HttpTimeout) {
                requestTimeoutMillis = TIMEOUT_MILLIS
                connectTimeoutMillis = TIMEOUT_MILLIS
                socketTimeoutMillis = TIMEOUT_MILLIS
            }

            defaultRequest {
                url {
                    protocol = URLProtocol.HTTPS
                    host = HOST
                    encodedPath = BASE_PATH
                }
            }
        }
    }
}
