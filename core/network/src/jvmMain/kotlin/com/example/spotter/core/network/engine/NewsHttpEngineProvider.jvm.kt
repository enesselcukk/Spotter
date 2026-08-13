package com.example.spotter.core.network.engine

import io.ktor.client.engine.HttpClientEngineFactory
import io.ktor.client.engine.cio.CIO

actual fun getProvide(): HttpClientEngineFactory<*> = CIO