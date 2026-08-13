package com.example.spotter.shared.localization

import androidx.compose.runtime.Composable
import androidx.compose.runtime.ProvidedValue

expect object LocalAppLocale {
    val current: String
        @Composable get

    @Composable
    infix fun provides(value: String?): ProvidedValue<*>
}

expect object LocalAppTheme {
    val current: Boolean
        @Composable get

    @Composable
    infix fun provides(value: Boolean?): ProvidedValue<*>
}

expect fun deviceLanguageTag(): String
