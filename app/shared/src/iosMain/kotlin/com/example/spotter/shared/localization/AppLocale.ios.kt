package com.example.spotter.shared.localization

import androidx.compose.runtime.Composable
import androidx.compose.runtime.ProvidedValue
import androidx.compose.runtime.staticCompositionLocalOf
import platform.Foundation.NSLocale
import platform.Foundation.NSUserDefaults
import platform.Foundation.preferredLanguages
import platform.UIKit.UIUserInterfaceStyle
import platform.UIKit.UIScreen

actual object LocalAppLocale {
    private const val LANG_KEY = "AppleLanguages"
    private val default = NSLocale.preferredLanguages.firstOrNull() as? String ?: "en"
    private val localeState = staticCompositionLocalOf { default }

    actual val current: String
        @Composable get() = localeState.current

    @Composable
    actual infix fun provides(value: String?): ProvidedValue<*> {
        val new = value ?: default
        if (value == null) {
            NSUserDefaults.standardUserDefaults.removeObjectForKey(LANG_KEY)
        } else {
            NSUserDefaults.standardUserDefaults.setObject(listOf(new), LANG_KEY)
        }
        return localeState.provides(new)
    }
}

actual object LocalAppTheme {
    private val themeState = staticCompositionLocalOf { isSystemDarkTheme() }

    actual val current: Boolean
        @Composable get() = themeState.current

    @Composable
    actual infix fun provides(value: Boolean?): ProvidedValue<*> {
        val resolved = value ?: isSystemDarkTheme()
        return themeState.provides(resolved)
    }
}

actual fun deviceLanguageTag(): String =
    (NSLocale.preferredLanguages.firstOrNull() as? String)?.substringBefore('-') ?: "en"

private fun isSystemDarkTheme(): Boolean =
    UIScreen.mainScreen.traitCollection.userInterfaceStyle ==
        UIUserInterfaceStyle.UIUserInterfaceStyleDark
