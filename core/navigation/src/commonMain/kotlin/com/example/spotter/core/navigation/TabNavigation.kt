package com.example.spotter.core.navigation

/**
 * Bottom tab switching keeps the back stack at most two entries deep — the root tab plus at
 * most one sibling tab — so system back always lands on the root tab instead of walking
 * through every tab the user visited.
 */
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
