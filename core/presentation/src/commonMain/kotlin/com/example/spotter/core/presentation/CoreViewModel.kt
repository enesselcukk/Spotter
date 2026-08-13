package com.example.spotter.core.presentation

import androidx.lifecycle.ViewModel
import com.example.spotter.core.domain.result.RestResult
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch

abstract class CoreViewModel : ViewModel() {

    protected inline fun <reified T : Any> safeFlowApiCall(
        crossinline call: () -> Flow<RestResult<T>>,
    ): Flow<RestResult<T>> =
        call().catch { throwable ->
            emit(RestResult.Error(throwable))
        }
}
