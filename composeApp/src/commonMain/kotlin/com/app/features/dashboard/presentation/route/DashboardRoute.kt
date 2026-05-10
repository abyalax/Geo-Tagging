package com.app.features.dashboard.presentation.route

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.lifecycle.viewmodel.compose.viewModel
import com.app.core.navigation.NavigationManager
import com.app.features.dashboard.presentation.screen.DashboardScreen
import com.app.features.dashboard.presentation.viewmodel.DashboardViewModel
import kotlinx.coroutines.flow.collectLatest

@Composable
fun DashboardRoute(
        navigationManager: NavigationManager,
        modifier: Modifier = Modifier,
        viewModel: DashboardViewModel = viewModel()
) {
    val uiState by viewModel.uiState.collectAsState()

    LaunchedEffect(viewModel.navigationEvents) {
        viewModel.navigationEvents.collectLatest { event ->
            event?.let { navigationManager.navigate(it) }
        }
    }

    DashboardScreen(
            uiState = uiState,
            onSurveyClick = { survey -> viewModel.onSurveyClick(survey) },
            onShareSurvey = { survey -> viewModel.onShareSurvey(survey) },
            onOpenSurveyMap = { survey -> viewModel.onOpenSurveyMap(survey) },
            onSearchChange = { query -> viewModel.onSearchChange(query) },
            onStatusFilterChange = { status -> viewModel.onStatusFilterChange(status) },
            onNavigateToProfile = { viewModel.onNavigateToProfile() },
            onNavigateToVerification = { surveyId, locationName ->
                viewModel.onNavigateToVerification(surveyId, locationName)
            },
            onNavigateToLogin = { viewModel.onNavigateToLogin() },
            onLogout = { viewModel.onLogout() },
            modifier = modifier
    )
}
