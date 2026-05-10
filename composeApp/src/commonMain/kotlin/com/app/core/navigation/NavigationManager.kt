package com.app.core.navigation

import Routes
import androidx.navigation.NavController
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.asSharedFlow

class NavigationManager {
    private val _navigationEvents = MutableSharedFlow<NavigationEvent>()
    val navigationEvents: SharedFlow<NavigationEvent> = _navigationEvents.asSharedFlow()

    fun navigate(event: NavigationEvent) {
        _navigationEvents.tryEmit(event)
    }

    fun handleNavigation(
        event: NavigationEvent,
        navController: NavController
    ) {
        when (event) {
            is NavigationEvent.NavigateToLogin -> {
                navController.navigate(Routes.Login.route) {
                    popUpTo(Routes.Dashboard.route) { inclusive = true }
                }
            }

            is NavigationEvent.NavigateToDashboard -> {
                navController.navigate(Routes.Dashboard.route) {
                    popUpTo(Routes.Splash.route) { inclusive = true }
                }
            }

            is NavigationEvent.NavigateToProfile -> {
                navController.navigate(Routes.Profile.route)
            }

            is NavigationEvent.NavigateBack -> {
                navController.popBackStack()
            }

            is NavigationEvent.NavigateToVerification -> {
                navController.navigate(
                    Routes.Verification.createRoute(event.surveyId, event.locationName)
                )
            }

            is NavigationEvent.NavigateWithPopUp -> {
                navController.navigate(event.route) {
                    popUpTo(event.popUpTo) { inclusive = event.inclusive }
                }
            }

            is NavigationEvent.ClearBackStackAndNavigate -> {
                navController.navigate(Routes.Dashboard.route) {
                    popUpTo(0) { inclusive = true }
                }
            }
        }
    }
}
