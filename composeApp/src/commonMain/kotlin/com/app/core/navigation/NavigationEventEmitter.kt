package com.app.core.navigation

import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.asSharedFlow

/**
 * Platform-agnostic navigation event emitter.
 * No Android dependencies. Works for both Android (Compose) and iOS (SwiftUI).
 *
 * Location: commonMain/core/navigation/NavigationEventEmitter.kt
 */
interface NavigationEventEmitter {
    val navigationEvents: SharedFlow<NavigationEvent>
    fun navigate(event: NavigationEvent)
}

/**
 * Default implementation. Move NavigationManager logic here.
 * This is the new centerpiece for commonMain.
 */
class DefaultNavigationEventEmitter : NavigationEventEmitter {
    private val _navigationEvents =
            MutableSharedFlow<NavigationEvent>(extraBufferCapacity = 1)
    override val navigationEvents: SharedFlow<NavigationEvent> = _navigationEvents.asSharedFlow()

    override fun navigate(event: NavigationEvent) {
        _navigationEvents.tryEmit(event)
    }
}
