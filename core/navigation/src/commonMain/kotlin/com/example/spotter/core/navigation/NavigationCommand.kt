package com.example.spotter.core.navigation

import androidx.navigation3.runtime.NavKey

sealed interface NavigationCommand {
    interface Destination : NavigationCommand, NavKey

    data class NavigateTo(
        val to: Destination,
        val clearBackStack: Boolean = false,
        val addToBackStack: Boolean = true,
    ) : NavigationCommand

    data object NavigateUp : NavigationCommand

    data class PopBackStackTo(
        val to: Destination,
        val inclusive: Boolean = false,
    ) : NavigationCommand
}
