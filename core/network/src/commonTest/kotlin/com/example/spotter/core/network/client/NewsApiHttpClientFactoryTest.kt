package com.example.spotter.core.network.client

import kotlin.test.Test
import kotlin.test.assertNotNull

class OverpassApiHttpClientFactoryTest {
    @Test
    fun create_isNotNull() {
        val client = OverpassApiHttpClientFactory.create(
            baseUrl = "https://example.com/v2/",
            enableLogging = false,
        )
        assertNotNull(client)
        client.close()
    }
}
