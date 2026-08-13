package com.example.spotter.core.datastore

enum class DefaultHomeViewMode {
    LIST,
    MAP,
}

fun defaultHomeViewModeFromStorage(value: String?): DefaultHomeViewMode =
    when (value) {
        "MAP" -> DefaultHomeViewMode.MAP
        else -> DefaultHomeViewMode.LIST
    }
