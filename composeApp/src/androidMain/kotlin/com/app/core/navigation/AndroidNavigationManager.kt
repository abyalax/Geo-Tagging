package com.app.core.navigation

import android.util.Log
import androidx.navigation.NavController
import com.app.navigation.Routes

/**
 * Android-specific navigation manager that handles NavController. This bridges the
 * platform-agnostic NavigationEventEmitter to Android's NavController.
 *
 * Location: androidMain/core/navigation/AndroidNavigationManager.kt
 */
class AndroidNavigationManager(private val emitter: NavigationEventEmitter) :
        NavigationEventEmitter by emitter {

    /**
     * Handle navigation events using Android NavController. This method should be called from the
     * Android UI layer.
     */
    fun handleNavigation(event: NavigationEvent, navController: NavController) {
        Log.d("AndroidNavigationManager", "=== Navigation event received: $event ===")
        Log.d(
                "AndroidNavigationManager",
                "NavController current destination: ${navController.currentDestination?.route}"
        )
        when (event) {
            is NavigationEvent.NavigateToLogin -> {
                Log.d("AndroidNavigationManager", "Executing: Navigate to Login")
                navController.navigate(Routes.Login.route) {
                    popUpTo(0) { inclusive = true }
                    launchSingleTop = true
                }
                Log.d("AndroidNavigationManager", "Navigation to Login completed")
            }
            is NavigationEvent.NavigateToDashboard -> {
                Log.d("AndroidNavigationManager", "Executing: Navigate to Dashboard")
                Log.d("AndroidNavigationManager", "Target route: ${Routes.Dashboard.route}")
                navController.navigate(Routes.Dashboard.route) {
                    popUpTo(Routes.Splash.route) { inclusive = true }
                    launchSingleTop = true
                }
                Log.d("AndroidNavigationManager", "Navigation to Dashboard completed")
            }
            is NavigationEvent.NavigateToProfile -> {
                Log.d("AndroidNavigationManager", "Navigating to Profile")
                navController.navigate(Routes.Profile.route)
            }
            is NavigationEvent.NavigateBack -> {
                Log.d("AndroidNavigationManager", "Navigating Back")
                navController.popBackStack()
            }
            is NavigationEvent.NavigateToVerification -> {
                Log.d(
                        "AndroidNavigationManager",
                        "Navigating to Verification with surveyId: ${event.surveyId}, locationName: ${event.locationName}"
                )
                navController.navigate(
                        Routes.Verification.createRoute(event.surveyId, event.locationName)
                )
            }
            is NavigationEvent.NavigateWithPopUp -> {
                Log.d(
                        "AndroidNavigationManager",
                        "Navigating to ${event.route} with popUpTo ${event.popUpTo}"
                )
                navController.navigate(event.route) {
                    popUpTo(event.popUpTo) { inclusive = event.inclusive }
                }
            }
            is NavigationEvent.ClearBackStackAndNavigate -> {
                Log.d("AndroidNavigationManager", "Clearing back stack and navigating to Dashboard")
                navController.navigate(Routes.Dashboard.route) { popUpTo(0) { inclusive = true } }
            }
        }
    }
}
