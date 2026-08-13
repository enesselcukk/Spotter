package com.example.spotter.feature.home.data.network

import com.example.spotter.core.data.exception.HttpStatusException
import com.example.spotter.core.network.config.OverpassApiConfig
import com.example.spotter.feature.home.domain.model.SpotSearchQuery
import io.ktor.client.HttpClient
import io.ktor.client.request.post
import io.ktor.client.request.setBody
import io.ktor.client.request.url
import io.ktor.client.statement.HttpResponse
import io.ktor.http.ContentType
import io.ktor.http.contentType
import io.ktor.http.isSuccess

class HomeApi(
    private val httpClient: HttpClient,
) {
    suspend fun getNearbySpots(query: SpotSearchQuery): HttpResponse {
        val body = OverpassQueryBuilder.build(query)
        var lastResponse: HttpResponse? = null

        for (host in OverpassApiConfig.HOSTS) {
            val response = httpClient.post {
                url("https://$host/api/interpreter")
                contentType(ContentType.Text.Plain)
                setBody(body)
            }
            if (response.status.isSuccess()) return response
            lastResponse = response
            if (response.status.value !in RETRYABLE_STATUS_CODES) return response
        }

        return lastResponse ?: throw HttpStatusException(
            statusCode = 503,
            message = "Overpass service unavailable",
        )
    }

    private companion object {
        val RETRYABLE_STATUS_CODES = setOf(408, 429, 500, 502, 503, 504, 524)
    }
}
