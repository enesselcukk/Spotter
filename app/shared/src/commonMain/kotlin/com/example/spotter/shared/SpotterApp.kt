package com.example.spotter.shared

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation3.runtime.entryProvider
import androidx.navigation3.runtime.rememberNavBackStack
import androidx.navigation3.ui.NavDisplay
import coil3.ImageLoader
import coil3.compose.setSingletonImageLoaderFactory
import coil3.network.ktor3.KtorNetworkFetcherFactory
import com.example.spotter.core.datastore.AppLanguage
import com.example.spotter.core.datastore.ThemeMode
import com.example.spotter.core.datastore.UserSettingsRepository
import com.example.spotter.core.designsystem.theme.SpotterTheme
import com.example.spotter.core.navigation.NavGraphProvider
import com.example.spotter.core.navigation.NavigationCommand
import com.example.spotter.core.navigation.NavigationManager
import com.example.spotter.feature.splash.contract.SplashScreenDestination
import com.example.spotter.shared.localization.LocalAppTheme
import com.example.spotter.shared.localization.SpotterAppEnvironment
import com.example.spotter.shared.localization.deviceLanguageTag
import com.example.spotter.shared.navigation.spotterNavSavedStateConfiguration
import org.koin.compose.koinInject
import org.koin.mp.KoinPlatform.getKoin

@Composable
fun SpotterApp() {
    setSingletonImageLoaderFactory { context ->
        ImageLoader.Builder(context)
            .components {
                add(KtorNetworkFetcherFactory())
            }.build()
    }

    val settingsRepository: UserSettingsRepository = koinInject()
    val themeMode by settingsRepository.themeMode.collectAsStateWithLifecycle(ThemeMode.SYSTEM)
    val language by settingsRepository.language.collectAsStateWithLifecycle(AppLanguage.ENGLISH.tag)
    val autoApplyLocalization by settingsRepository.autoApplyLocalization.collectAsStateWithLifecycle(true)

    val languageTag = if (autoApplyLocalization) {
        AppLanguage.normalizeDeviceTag(deviceLanguageTag())
    } else {
        language
    }

    val themeOverride = when (themeMode) {
        ThemeMode.SYSTEM -> null
        ThemeMode.LIGHT -> false
        ThemeMode.DARK -> true
    }

    val backStack = rememberNavBackStack(spotterNavSavedStateConfiguration, SplashScreenDestination)
    val providers: List<NavGraphProvider> = getKoin().getAll()
    val navigationManager: NavigationManager = koinInject()

    LaunchedEffect(navigationManager, backStack) {
        navigationManager.navigationCommandFlow.collect { command ->
            when (command) {
                is NavigationCommand.NavigateTo -> {
                    when {
                        command.clearBackStack -> backStack.clear()
                        !command.addToBackStack -> backStack.removeLastOrNull()
                    }
                    backStack.add(command.to)
                }

                NavigationCommand.NavigateUp -> {
                    if (backStack.size > 1) backStack.removeLastOrNull()
                }

                is NavigationCommand.Destination -> backStack.add(command)

                is NavigationCommand.PopBackStackTo -> {
                    val index = backStack.indexOfLast { it == command.to }
                    if (index >= 0) {
                        val targetSize = if (command.inclusive) index else index + 1
                        while (backStack.size > targetSize) {
                            backStack.removeLastOrNull()
                        }
                    }
                }
            }
        }
    }

    SpotterAppEnvironment(
        languageTag = languageTag,
        darkTheme = themeOverride,
    ) {
        SpotterTheme(darkTheme = LocalAppTheme.current) {
            NavDisplay(
                modifier = Modifier.fillMaxSize(),
                backStack = backStack,
                onBack = { backStack.removeLastOrNull() },
                entryProvider = entryProvider {
                    providers.forEach { provider ->
                        provider.registerEntries(this)
                    }
                },
            )
        }
    }
}
