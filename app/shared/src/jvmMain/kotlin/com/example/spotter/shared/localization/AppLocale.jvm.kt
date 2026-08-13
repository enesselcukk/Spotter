package com.example.spotter.shared.localization

import androidx.compose.runtime.Composable
import androidx.compose.runtime.ProvidedValue
import androidx.compose.runtime.staticCompositionLocalOf
import java.util.Locale

actual object LocalAppLocale {
    private var default: Locale? = null
    private val localeState = staticCompositionLocalOf { Locale.getDefault().toLanguageTag() }

    actual val current: String
        @Composable get() = localeState.current

    @Composable
    actual infix fun provides(value: String?): ProvidedValue<*> {
        if (default == null) {
            default = Locale.getDefault()
        }
        val locale = when (value) {
            null -> default!!
            else -> Locale.forLanguageTag(value)
        }
        Locale.setDefault(locale)
        return localeState.provides(locale.toLanguageTag())
    }
}

actual object LocalAppTheme {
    private val themeState = staticCompositionLocalOf {
        javax.swing.UIManager.getLookAndFeel().isDark
    }

    actual val current: Boolean
        @Composable get() = themeState.current

    @Composable
    actual infix fun provides(value: Boolean?): ProvidedValue<*> {
        val resolved = value ?: isSystemDarkTheme()
        return themeState.provides(resolved)
    }
}

actual fun deviceLanguageTag(): String = Locale.getDefault().language

private fun isSystemDarkTheme(): Boolean = false
