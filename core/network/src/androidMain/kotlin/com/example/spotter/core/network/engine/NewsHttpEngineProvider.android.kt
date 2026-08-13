package com.example.spotter.core.network.engine

import io.ktor.client.engine.HttpClientEngineFactory
import io.ktor.client.engine.okhttp.OkHttp

actual fun getProvide(): HttpClientEngineFactory<*> = OkHttp

