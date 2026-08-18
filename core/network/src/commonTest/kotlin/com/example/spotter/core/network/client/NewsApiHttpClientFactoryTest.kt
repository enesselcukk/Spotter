package com.example.spotter.core.network.client

import kotlin.test.Test
import kotlin.test.assertNotNull

class OverpassApiHttpClientFactoryTest {
    @Test
    fun create_isNotNull() {
        val client = OverpassApiHttpClientFactory.create(enableLogging = false)
        assertNotNull(client)
        client.close()
    }

    @Test
    fun createDirect_isNotNull() {
        val client = OverpassApiHttpClientFactory.createDirect(enableLogging = false)
        assertNotNull(client)
        client.close()
    }
}
