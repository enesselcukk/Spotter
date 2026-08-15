package com.example.spotter.core.navigation

fun NavigationManager.switchTab(
    target: NavigationCommand.Destination,
    root: NavigationCommand.Destination,
    currentIsRoot: Boolean,
) {
    if (target == root) {
        navigate(NavigationCommand.PopBackStackTo(to = root))
        return
    }

    navigate(
        NavigationCommand.NavigateTo(
            to = target,
            addToBackStack = currentIsRoot,
        ),
    )
}
