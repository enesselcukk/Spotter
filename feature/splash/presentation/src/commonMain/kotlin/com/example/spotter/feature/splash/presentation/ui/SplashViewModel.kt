package com.example.spotter.feature.splash.presentation.ui

import androidx.lifecycle.viewModelScope
import com.example.spotter.core.navigation.NavigationCommand
import com.example.spotter.core.navigation.NavigationManager
import com.example.spotter.core.presentation.CoreViewModel
import com.example.spotter.feature.home.contract.HomeScreenDestination
import com.example.spotter.feature.home.domain.usecase.PreloadHomeDataUseCase
import kotlinx.coroutines.TimeoutCancellationException
import kotlinx.coroutines.async
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.withTimeout

class SplashViewModel(
    private val preloadHomeDataUseCase: PreloadHomeDataUseCase,
    private val navigationManager: NavigationManager,
) : CoreViewModel() {

    private val _progress = MutableStateFlow(0f)
    val progress: StateFlow<Float> = _progress.asStateFlow()

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
            navigationManager.navigate(
                NavigationCommand.NavigateTo(
                    to = HomeScreenDestination,
                    clearBackStack = true,
                ),
            )
        }
    }

    private companion object {
        const val MIN_SPLASH_DURATION_MS = 2_000L
        const val PRELOAD_TIMEOUT_MS = 35_000L
    }
}
