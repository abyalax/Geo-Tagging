package com.app.core.navigation

import Routes
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Modifier
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.app.core.domain.usecase.auth.GetSessionUseCase
import com.app.features.auth.presentation.route.LoginRoute
import com.app.features.auth.presentation.route.SplashScreenRoute
import com.app.features.dashboard.presentation.route.DashboardRoute
import com.app.features.profile.presentation.route.ProfileRoute
import com.app.features.verification.presentation.route.VerificationRoute

@Composable
fun AppNavHost(
    navController: NavHostController = rememberNavController(),
    startDestination: String = Routes.Splash.route,
    navigationManager: NavigationManager,
    getSessionUseCase: GetSessionUseCase,
    modifier: Modifier = Modifier
) {
    LaunchedEffect(navigationManager.navigationEvents) {
        navigationManager.navigationEvents.collect { event ->
            navigationManager.handleNavigation(event, navController)
        }
    }

    NavHost(
        navController = navController,
        startDestination = startDestination,
        modifier = modifier
    ) {
        composable(Routes.Splash.route) {
            SplashScreenRoute(
                navigationManager = navigationManager,
                getSessionUseCase = getSessionUseCase
            )
        }

        composable(Routes.Login.route) { LoginRoute(navigationManager = navigationManager) }

        composable(Routes.Dashboard.route) { DashboardRoute(navigationManager = navigationManager) }

        composable(Routes.Profile.route) { ProfileRoute(navigationManager = navigationManager) }

        composable(
            route = Routes.Verification.route,
            arguments = Routes.Verification.arguments
        ) { backStackEntry ->
            val surveyId = backStackEntry.arguments?.getString("surveyId") ?: ""
            val locationName = backStackEntry.arguments?.getString("locationName") ?: ""

            VerificationRoute(
                surveyId = surveyId,
                locationName = locationName,
                navigationManager = navigationManager
            )
        }
    }
}
