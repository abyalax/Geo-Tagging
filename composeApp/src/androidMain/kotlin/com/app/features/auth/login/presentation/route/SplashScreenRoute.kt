package com.app.features.auth.login.presentation.route

import android.util.Log
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import com.app.core.domain.usecase.auth.GetSessionUseCase
import com.app.core.navigation.NavigationEvent
import com.app.core.navigation.NavigationEventEmitter
import kotlinx.coroutines.flow.first

/**
 * SplashScreenRoute - Composable route for Splash screen.
 *
 * FIXES: ✅ Accept navigationEventEmitter instead of NavController ✅ Use getSessionUseCase to check
 * login state ✅ Emit navigation event (no direct NavController manipulation) ✅ Let AppNavHost
 * handle the actual navigation
 */
@Composable
fun SplashScreenRoute(
        navigationEventEmitter: NavigationEventEmitter, // ✅ Use emitter, not NavController
        getSessionUseCase: GetSessionUseCase
) {
    Log.d("SplashScreenRoute", "SplashScreenRoute started")

    LaunchedEffect(Unit) {
        try {
            Log.d("SplashScreenRoute", "Checking login state...")

            val session = getSessionUseCase().first()
            Log.d("SplashScreenRoute", "Session state: isLoggedIn=${session.isLoggedIn}")

            if (session.isLoggedIn) {
                Log.d("SplashScreenRoute", "✅ User is logged in - navigating to Dashboard")
                navigationEventEmitter.navigate(NavigationEvent.NavigateToDashboard)
            } else {
                Log.d("SplashScreenRoute", "❌ User not logged in - navigating to Login")
                navigationEventEmitter.navigate(NavigationEvent.NavigateToLogin)
            }
        } catch (e: Exception) {
            Log.e("SplashScreenRoute", "❌ Error checking session: ${e.message}", e)
            navigationEventEmitter.navigate(NavigationEvent.NavigateToLogin)
        }
    }

    // UI: Show splash/loading screen
    Box(
            modifier = Modifier.fillMaxSize().background(MaterialTheme.colorScheme.primary),
            contentAlignment = Alignment.Center
    ) { CircularProgressIndicator(color = MaterialTheme.colorScheme.onPrimary) }
}
