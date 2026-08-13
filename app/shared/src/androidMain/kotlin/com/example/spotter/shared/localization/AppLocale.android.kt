package com.example.spotter.shared.localization

import android.content.res.Configuration
import androidx.compose.runtime.Composable
import androidx.compose.runtime.ProvidedValue
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalContext
import java.util.Locale

actual object LocalAppLocale {
    private var default: Locale? = null

    actual val current: String
        @Composable get() = Locale.getDefault().toLanguageTag()

    @Composable
    actual infix fun provides(value: String?): ProvidedValue<*> {
        val configuration = LocalConfiguration.current
        if (default == null) {
            default = Locale.getDefault()
        }
        val locale = when (value) {
            null -> default!!
            else -> Locale.forLanguageTag(value)
        }
        Locale.setDefault(locale)
        val updatedConfiguration = Configuration(configuration)
        updatedConfiguration.setLocale(locale)
        @Suppress("DEPRECATION")
        LocalContext.current.resources.updateConfiguration(
            updatedConfiguration,
            LocalContext.current.resources.displayMetrics,
        )
        return LocalConfiguration.provides(updatedConfiguration)
    }
}

actual object LocalAppTheme {
    actual val current: Boolean
        @Composable get() =
            (LocalConfiguration.current.uiMode and Configuration.UI_MODE_NIGHT_MASK) ==
                Configuration.UI_MODE_NIGHT_YES

    @Composable
    actual infix fun provides(value: Boolean?): ProvidedValue<*> {
        val updatedConfiguration = if (value == null) {
            LocalConfiguration.current
        } else {
            Configuration(LocalConfiguration.current).apply {
                uiMode = when (value) {
                    true -> (uiMode and Configuration.UI_MODE_NIGHT_MASK.inv()) or Configuration.UI_MODE_NIGHT_YES
                    false -> (uiMode and Configuration.UI_MODE_NIGHT_MASK.inv()) or Configuration.UI_MODE_NIGHT_NO
                }
            }
        }
        return LocalConfiguration.provides(updatedConfiguration)
    }
}

actual fun deviceLanguageTag(): String = Locale.getDefault().language
