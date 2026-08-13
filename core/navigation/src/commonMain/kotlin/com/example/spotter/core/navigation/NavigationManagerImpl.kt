package com.example.spotter.core.navigation

import kotlin.coroutines.CoroutineContext
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.launch

class NavigationManagerImpl : CoroutineScope, NavigationManager {

    override val coroutineContext: CoroutineContext = Dispatchers.Main + SupervisorJob()

    private val navigationCommandChannel = Channel<NavigationCommand>()
    override val navigationCommandFlow: Flow<NavigationCommand>
        get() = navigationCommandChannel.receiveAsFlow()

    override fun navigate(navigationCommand: NavigationCommand) {
        launch {
            navigationCommandChannel.send(navigationCommand)
        }
    }
}
