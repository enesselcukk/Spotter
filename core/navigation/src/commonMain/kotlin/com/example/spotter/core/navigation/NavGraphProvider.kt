package com.example.spotter.core.navigation

import androidx.navigation3.runtime.EntryProviderScope
import androidx.navigation3.runtime.NavKey

fun interface NavGraphProvider {
    fun registerEntries(scope: EntryProviderScope<NavKey>)
}
