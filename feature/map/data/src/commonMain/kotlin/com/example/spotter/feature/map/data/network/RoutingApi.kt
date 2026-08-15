package com.example.spotter.feature.map.data.network

import com.example.spotter.feature.map.domain.model.RoutePoint
import com.example.spotter.feature.map.domain.model.TravelMode
import io.ktor.client.HttpClient
import io.ktor.client.request.get
import io.ktor.client.request.url
import io.ktor.client.statement.HttpResponse

class RoutingApi(
    private val httpClient: HttpClient,
) {
    suspend fun getRoute(
        origin: RoutePoint,
        destination: RoutePoint,
        travelMode: TravelMode,
    ): HttpResponse {
        val coordinates = "${origin.longitude},${origin.latitude};" +
            "${destination.longitude},${destination.latitude}"

        return httpClient.get {
            url(
                "$BASE_URL/route/v1/${travelMode.apiProfile}/$coordinates" +
                    "?overview=full&geometries=polyline&steps=true&alternatives=false&annotations=false",
            )
        }
    }

    private companion object {
        const val BASE_URL = "https://router.project-osrm.org"
    }
}
