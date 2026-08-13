package com.example.spotter.core.data.dispatcher

import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers

internal actual val repositoryIoDispatcher: CoroutineDispatcher = Dispatchers.Default
