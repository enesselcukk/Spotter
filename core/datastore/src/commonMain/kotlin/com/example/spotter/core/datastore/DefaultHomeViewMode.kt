package com.example.spotter.core.datastore

enum class DefaultHomeViewMode {
    LIST,
    COLUMN,
}

fun defaultHomeViewModeFromStorage(value: String?): DefaultHomeViewMode =
    when (value) {
        "COLUMN", "MAP" -> DefaultHomeViewMode.COLUMN
        else -> DefaultHomeViewMode.LIST
    }
