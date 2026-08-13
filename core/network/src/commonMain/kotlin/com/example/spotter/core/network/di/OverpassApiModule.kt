package com.example.spotter.core.network.di

import com.example.spotter.core.network.client.OverpassApiHttpClientFactory
import com.example.spotter.core.network.config.OverpassApiConfig
import io.ktor.client.HttpClient
import org.koin.dsl.module

val overpassApiModule = module {
    single<HttpClient> {
        OverpassApiHttpClientFactory.create(
            enableLogging = true)
    }
}
