package com.example.spotter.feature.splash.presentation.ui

import androidx.lifecycle.viewModelScope
import com.example.spotter.core.presentation.CoreViewModel
import com.example.spotter.feature.home.domain.usecase.PreloadHomeDataUseCase
import kotlinx.coroutines.TimeoutCancellationException
import kotlinx.coroutines.async
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.withTimeout

class SplashViewModel(
    private val preloadHomeDataUseCase: PreloadHomeDataUseCase,
) : CoreViewModel() {

    private val _progress = MutableStateFlow(0f)
    val progress: StateFlow<Float> = _progress.asStateFlow()

    private val _navigateHome = MutableSharedFlow<Unit>(extraBufferCapacity = 1)
    val navigateHome: SharedFlow<Unit> = _navigateHome.asSharedFlow()

    private var started = false

    fun startPreloadIfNeeded() {
        if (started) return
        started = true

        viewModelScope.launch {
            val minimumSplash = async { delay(MIN_SPLASH_DURATION_MS) }
            val preloadJob = async {
                try {
                    withTimeout(PRELOAD_TIMEOUT_MS) {
                        preloadHomeDataUseCase { value ->
                            _progress.value = value.coerceIn(0f, 1f)
                        }
                    }
                } catch (_: TimeoutCancellationException) {
                    preloadHomeDataUseCase.ensureFallbackSaved()
                    _progress.value = 1f
                }
            }

            preloadJob.await()
            _progress.value = 1f
            minimumSplash.await()
            _navigateHome.emit(Unit)
        }
    }

    private companion object {
        const val MIN_SPLASH_DURATION_MS = 2_000L
        const val PRELOAD_TIMEOUT_MS = 35_000L
    }
}
