package com.example.spotter.core.datastore

enum class ThemeMode {
    SYSTEM,
    LIGHT,
    DARK,
}

fun themeModeFromStorage(value: String?): ThemeMode =
    when (value) {
        "LIGHT" -> ThemeMode.LIGHT
        "DARK" -> ThemeMode.DARK
        else -> ThemeMode.SYSTEM
    }
