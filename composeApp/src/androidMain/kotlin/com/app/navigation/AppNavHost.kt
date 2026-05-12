package com.app.navigation

import android.net.Uri
import android.util.Log
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.app.core.data.repository.AuthRepositoryImpl
import com.app.core.domain.usecase.auth.GetSessionUseCase
import com.app.core.navigation.AndroidNavigationManager
import com.app.core.navigation.NavigationEventEmitter
import com.app.features.auth.login.presentation.route.LoginRoute
import com.app.features.auth.login.presentation.route.SplashScreenRoute
import com.app.features.auth.login.presentation.viewmodel.LoginViewModel
import com.app.features.dashboard.presentation.route.DashboardRoute
import com.app.features.dashboard.ui.screen.VerificationScreen
import com.app.features.dashboard.viewmodel.DashboardViewModel
import com.app.features.profile.presentation.route.ProfileRoute
import com.app.features.profile.presentation.viewmodel.ProfileViewModel

@Composable
fun AppNavHost(
        navController: NavHostController = rememberNavController(),
        startDestination: String = Routes.Splash.route,
        navigationEventEmitter: NavigationEventEmitter,
        androidNavigationManager: AndroidNavigationManager,
        getSessionUseCase: GetSessionUseCase,
        loginViewModel: LoginViewModel,
        dashboardViewModel: DashboardViewModel,
        modifier: Modifier = Modifier
) {
    Log.d("AppNavHost", "AppNavHost initialized")
    Log.d("AppNavHost", "Start destination: $startDestination")
    val context = LocalContext.current
    val currentRoute = navController.currentBackStackEntryAsState().value?.destination?.route
    val isLoggedIn =
            context.getSharedPreferences("app_prefs", android.content.Context.MODE_PRIVATE)
                    .getBoolean("is_logged_in", false)

    LaunchedEffect(navigationEventEmitter.navigationEvents) {
        Log.d("AppNavHost", "=== Navigation events listener started ===")
        navigationEventEmitter.navigationEvents.collect { event ->
            Log.d("AppNavHost", "=== AppNavHost received navigation event: $event ===")
            androidNavigationManager.handleNavigation(event, navController)
        }
    }

    LaunchedEffect(navController) {
        navController.currentBackStackEntryFlow.collect { entry ->
            if (entry.destination.route == Routes.Dashboard.route) {
                dashboardViewModel.refresh()
            }
        }
    }

    AuthMiddleware(
            isLoggedIn = isLoggedIn,
            currentRoute = currentRoute,
            onNavigateToLogin = {
                navController.navigate(Routes.Login.route) {
                    popUpTo(0) { inclusive = true }
                    launchSingleTop = true
                }
            }
    ) {
        NavHost(
                navController = navController,
                startDestination = startDestination,
                modifier = modifier
        ) {
            composable(Routes.Splash.route) {
                SplashScreenRoute(
                        navigationEventEmitter = navigationEventEmitter,
                        getSessionUseCase = getSessionUseCase
                )
            }

            composable(Routes.Login.route) {
                LoginRoute(
                        navigationEventEmitter = navigationEventEmitter,
                        viewModel = loginViewModel
                )
            }

            composable(Routes.Dashboard.route) {
                DashboardRoute(
                        navigationEventEmitter = navigationEventEmitter,
                        viewModel = dashboardViewModel,
                        navController = navController
                )
            }

            composable(Routes.Profile.route) {
                val context = LocalContext.current
                val profileViewModel =
                        androidx.compose.runtime.remember {
                            ProfileViewModel(
                                    authRepository = AuthRepositoryImpl(context),
                                    getSessionUseCase = getSessionUseCase,
                                    context = context
                            )
                        }

                ProfileRoute(
                        navigationEventEmitter = navigationEventEmitter,
                        viewModel = profileViewModel,
                        modifier = Modifier
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
                val surveyId = Uri.decode(backStackEntry.arguments?.getString("surveyId") ?: "")
                val locationName =
                        Uri.decode(backStackEntry.arguments?.getString("locationName") ?: "")
                val username =
                        context.getSharedPreferences("app_prefs", android.content.Context.MODE_PRIVATE)
                                .getString("username", "")
                                ?: ""

                VerificationScreen(
                        surveyId = surveyId,
                        locationName = locationName,
                        username = username,
                        onSuccess = { _ -> },
                        onCancel = { navController.popBackStack() },
                        onNavigateToHome = {
                            if (!navController.popBackStack(Routes.Dashboard.route, false)) {
                                navController.navigate(Routes.Dashboard.route) {
                                    popUpTo(Routes.Verification.route) { inclusive = true }
                                }
                            }
                        },
                        navController = navController,
                        onNavigateToLogin = {
                            navController.navigate(Routes.Login.route) {
                                popUpTo(0) { inclusive = true }
                                launchSingleTop = true
                            }
                        },
                        onNavigateToProfile = { controller ->
                            controller.navigate(Routes.Profile.route)
                        }
                )
            }
        }
    }
}
