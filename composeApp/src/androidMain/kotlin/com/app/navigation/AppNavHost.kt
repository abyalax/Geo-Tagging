package com.app.navigation

import android.util.Log
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.app.core.navigation.AuthMiddleware
import com.app.features.auth.login.ui.screen.LoginScreen
import com.app.features.dashboard.ui.screen.DashboardScreen
import com.app.features.dashboard.ui.screen.VerificationScreen
import com.app.features.dashboard.viewmodel.DashboardViewModel
import com.app.features.profile.ui.screen.ProfileScreen

@Composable
fun AppNavHost(
        navController: NavHostController = rememberNavController(),
        startDestination: String = Routes.Splash.route
) {
    NavHost(navController = navController, startDestination = startDestination) {
        composable(Routes.Splash.route) { SplashScreen(navController = navController) }

        composable(Routes.Login.route) {
            val context = LocalContext.current
            LoginScreen(
                    onNavigateToDashboard = { username, password ->
                        Log.d("AppNavHost", "Login navigation callback triggered")
                        Log.d("AppNavHost", "Username: '$username', Password: '$password'")
                        // Save login state and navigate
                        Log.d("AppNavHost", "Calling AuthMiddleware.login")
                        AuthMiddleware.login(context, username, password)
                        Log.d(
                                "AppNavHost",
                                "Navigating to Dashboard route: ${Routes.Dashboard.route}"
                        )
                        navController.navigate(Routes.Dashboard.route) {
                            popUpTo(Routes.Login.route) { inclusive = true }
                        }
                        Log.d("AppNavHost", "Navigation command executed")
                    }
            )
        }

        composable(Routes.Dashboard.route) {
            val context = LocalContext.current
            val username = AuthMiddleware.getUsername(context) ?: "Field Officer"
            val viewModel: DashboardViewModel = androidx.lifecycle.viewmodel.compose.viewModel()

            // Collect StateFlow properly
            val surveys by viewModel.filteredSurveys.collectAsState()
            val isLoading by viewModel.isLoading.collectAsState()
            val searchQuery by viewModel.searchQuery.collectAsState()
            val selectedStatus by viewModel.selectedStatusFilter.collectAsState()
            val surveyStats = viewModel.getSurveyStats()

            DashboardScreen(
                    surveys = surveys,
                    isLoading = isLoading,
                    onSurveyClick = { survey ->
                        navController.navigate(
                                Routes.Verification.createRoute(survey.id.toString(), survey.title)
                        )
                    },
                    onShareSurvey = { survey ->
                        IntentNavigation.shareSurveyViaWhatsApp(context, survey)
                    },
                    onOpenSurveyMap = { survey ->
                        IntentNavigation.openSurveyInGoogleMaps(context, survey)
                    },
                    searchQuery = searchQuery,
                    onSearchChange = viewModel::searchSurveys,
                    selectedStatus = selectedStatus,
                    onStatusFilterChange = viewModel::filterByStatus,
                    username = username,
                    onNavigateToProfile = { navController.navigate(Routes.Profile.route) },
                    onNavigateToVerification = { surveyId, locationName ->
                        navController.navigate(
                                Routes.Verification.createRoute(surveyId, locationName)
                        )
                    },
                    onNavigateToLogin = {
                        navController.navigate(Routes.Login.route) {
                            popUpTo(Routes.Dashboard.route) { inclusive = true }
                        }
                    },
                    onLogout = { AuthMiddleware.logout(context, navController) },
                    surveyStats = surveyStats
            )
        }

        composable(Routes.Profile.route) {
            val context = LocalContext.current
            val prefs =
                    context.getSharedPreferences("app_prefs", android.content.Context.MODE_PRIVATE)
            val username = prefs.getString("username", "") ?: "User"
            val password = prefs.getString("password", "") ?: ""

            ProfileScreen(
                    username = username,
                    password = password,
                    onUsernameChange = { newUsername ->
                        prefs.edit().putString("username", newUsername).apply()
                    },
                    onPasswordChange = { newPassword ->
                        prefs.edit().putString("password", newPassword).apply()
                    },
                    onSaveProfile = {
                        // Profile already saved via onUsernameChange/onPasswordChange
                    },
                    onNavigateBack = { navController.popBackStack() },
                    onNavigateToLogin = {
                        navController.navigate(Routes.Login.route) {
                            popUpTo(Routes.Profile.route) { inclusive = true }
                        }
                    },
                    onLogout = { AuthMiddleware.logout(context, navController) }
            )
        }

        composable(
                route = Routes.Verification.route,
                arguments =
                        listOf(
                                navArgument("surveyId") { type = NavType.StringType },
                                navArgument("locationName") { type = NavType.StringType }
                        )
        ) { backStackEntry ->
            val context = LocalContext.current
            val surveyId = backStackEntry.arguments?.getString("surveyId") ?: ""
            val locationName = backStackEntry.arguments?.getString("locationName") ?: ""
            val username = AuthMiddleware.getUsername(context) ?: ""

            VerificationScreen(
                    surveyId = surveyId,
                    locationName = locationName,
                    username = username,
                    onSuccess = { _ ->
                        // Handle verification success
                    },
                    onCancel = { navController.popBackStack() },
                    onNavigateToHome = {
                        navController.navigate(Routes.Dashboard.route) {
                            popUpTo(Routes.Verification.route) { inclusive = true }
                        }
                    },
                    navController = navController,
                    onNavigateToLogin = {
                        navController.navigate(Routes.Login.route) {
                            popUpTo(Routes.Verification.route) { inclusive = true }
                        }
                    },
                    onNavigateToProfile = { controller ->
                        controller.navigate(Routes.Profile.route)
                    }
            )
        }
    }
}

@Composable
fun SplashScreen(navController: NavHostController) {
    val context = LocalContext.current

    LaunchedEffect(Unit) {
        // Check authentication state
        if (AuthMiddleware.isLoggedIn(context)) {
            navController.navigate(Routes.Dashboard.route) {
                popUpTo(Routes.Splash.route) { inclusive = true }
            }
        } else {
            navController.navigate(Routes.Login.route) {
                popUpTo(Routes.Splash.route) { inclusive = true }
            }
        }
    }

    Box(
            modifier = Modifier.fillMaxSize().background(MaterialTheme.colorScheme.primary),
            contentAlignment = Alignment.Center
    ) { CircularProgressIndicator(color = MaterialTheme.colorScheme.onPrimary) }
}
