package com.example.spotter.core.network.di

import com.example.spotter.core.network.client.OverpassApiHttpClientFactory
import io.ktor.client.HttpClient
import org.koin.core.qualifier.named
import org.koin.dsl.module

val overpassApiModule = module {
    single<HttpClient> {
        OverpassApiHttpClientFactory.create(
            enableLogging = true)
    }
    single<HttpClient>(named("overpassDirect")) {
        OverpassApiHttpClientFactory.createDirect(
            enableLogging = true,
        )
    }
}
