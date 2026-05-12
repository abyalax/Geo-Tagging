package com.app.navigation

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect

@Composable
fun AuthMiddleware(
        isLoggedIn: Boolean,
        currentRoute: String?,
        onNavigateToLogin: () -> Unit,
        content: @Composable () -> Unit
) {
    val protectedRoute =
            currentRoute == Routes.Dashboard.route ||
                    currentRoute == Routes.Profile.route ||
                    currentRoute == Routes.Verification.route

    LaunchedEffect(isLoggedIn, currentRoute) {
        if (!isLoggedIn && protectedRoute) {
            onNavigateToLogin()
        }
    }

    content()
}
