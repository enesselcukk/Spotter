package com.example.spotter.core.network.engine

import io.ktor.client.engine.HttpClientEngineFactory

expect fun getProvide(): HttpClientEngineFactory<*>
