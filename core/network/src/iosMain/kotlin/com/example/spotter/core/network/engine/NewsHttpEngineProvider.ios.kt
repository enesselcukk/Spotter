package com.example.spotter.core.network.engine

import io.ktor.client.engine.HttpClientEngineFactory
import io.ktor.client.engine.darwin.Darwin

actual fun getProvide(): HttpClientEngineFactory<*> = Darwin
