package com.example.spotter.shared

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import coil3.ImageLoader
import coil3.compose.setSingletonImageLoaderFactory
import coil3.network.ktor3.KtorNetworkFetcherFactory
import com.example.spotter.core.datastore.AppLanguage
import com.example.spotter.core.datastore.ThemeMode
import com.example.spotter.core.datastore.UserSettingsRepository
import com.example.spotter.core.designsystem.theme.SpotterTheme
import com.example.spotter.core.navigation.NavGraphProvider
import com.example.spotter.feature.home.contract.HomeScreenDestination
import com.example.spotter.feature.splash.contract.SplashScreenDestination
import com.example.spotter.feature.splash.presentation.ui.SplashScreen
import com.example.spotter.shared.localization.LocalAppTheme
import com.example.spotter.shared.localization.SpotterAppEnvironment
import com.example.spotter.shared.localization.deviceLanguageTag
import org.koin.compose.koinInject
import org.koin.mp.KoinPlatform.getKoin

@Composable
fun SpotterApp() {
    setSingletonImageLoaderFactory { context ->
        ImageLoader.Builder(context)
            .components {
                add(KtorNetworkFetcherFactory())
            }
            .build()
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

    val navController = rememberNavController()
    val providers: List<NavGraphProvider> = getKoin().getAll()

    SpotterAppEnvironment(
        languageTag = languageTag,
        darkTheme = themeOverride,
    ) {
        SpotterTheme(darkTheme = LocalAppTheme.current) {
            NavHost(
                modifier = Modifier.fillMaxSize(),
                navController = navController,
                startDestination = SplashScreenDestination,
            ) {
                composable<SplashScreenDestination> {
                    SplashScreen(
                        onNavigateHome = {
                            navController.navigate(HomeScreenDestination) {
                                popUpTo(SplashScreenDestination) {
                                    inclusive = true
                                }
                            }
                        },
                    )
                }

                providers.forEach { provider ->
                    provider.registerGraph(provider = this)
                }
            }
        }
    }
}
