package com.app.features.dashboard.presentation.route

import android.util.Log
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import androidx.navigation.compose.rememberNavController
import com.app.core.navigation.NavigationEventEmitter
import com.app.features.dashboard.domain.model.SurveyStatus
import com.app.features.dashboard.model.Survey as UiSurvey
import com.app.features.dashboard.model.SurveyStats as UiSurveyStats
import com.app.features.dashboard.model.SurveyStatus as UiSurveyStatus
import com.app.features.dashboard.ui.screen.DashboardScreen
import com.app.features.dashboard.viewmodel.DashboardViewModel
import com.app.navigation.IntentNavigation
import kotlinx.coroutines.flow.collectLatest

/** DashboardRoute - Composable route for Dashboard screen. */
@Composable
fun DashboardRoute(
        navigationEventEmitter: NavigationEventEmitter, // ✅ Accept emitter
        modifier: Modifier = Modifier,
        viewModel: DashboardViewModel,
        navController: NavHostController = rememberNavController()
) {
        val uiState by viewModel.uiState.collectAsState()
        val context = LocalContext.current

        Log.d("DashboardRoute", "DashboardRoute started")
        Log.d("DashboardRoute", "Surveys count: ${uiState.surveys.size}")

        // ✅ CRITICAL: Listen to ViewModel's navigation events
        LaunchedEffect(viewModel.navigationEvents) {
                Log.d("DashboardRoute", "Navigation events listener started")
                viewModel.navigationEvents.collectLatest { event ->
                        event?.let {
                                Log.d("DashboardRoute", "Navigation event received: $it")
                                // ✅ Forward to global navigationEventEmitter
                                navigationEventEmitter.navigate(it)
                        }
                }
        }

        DashboardScreen(
                isLoading = uiState.isLoading,
                surveys =
                        uiState.surveys.map { domainSurvey ->
                                UiSurvey(
                                        id = domainSurvey.id,
                                        title = domainSurvey.title,
                                        description = domainSurvey.description,
                                        latitude = domainSurvey.latitude,
                                        longitude = domainSurvey.longitude,
                                        status =
                                                when (domainSurvey.status) {
                                                        com.app.features.dashboard.domain.model
                                                                .SurveyStatus.OPEN ->
                                                                UiSurveyStatus.OPEN
                                                        com.app.features.dashboard.domain.model
                                                                .SurveyStatus.VERIFIED ->
                                                                UiSurveyStatus.VERIFIED
                                                        com.app.features.dashboard.domain.model
                                                                .SurveyStatus.REJECTED ->
                                                                UiSurveyStatus.REJECTED
                                                },
                                        location = domainSurvey.location
                                )
                        },
                onSurveyClick = { survey ->
                        val domainSurvey =
                                com.app.features.dashboard.domain.model.Survey(
                                        id = survey.id,
                                        title = survey.title,
                                        description = survey.description,
                                        latitude = survey.latitude,
                                        longitude = survey.longitude,
                                        status =
                                                when (survey.status) {
                                                        UiSurveyStatus.OPEN -> SurveyStatus.OPEN
                                                        UiSurveyStatus.VERIFIED ->
                                                                SurveyStatus.VERIFIED
                                                        UiSurveyStatus.REJECTED ->
                                                                SurveyStatus.REJECTED
                                                },
                                        location =
                                                survey.location.ifEmpty {
                                                        "${survey.latitude}, ${survey.longitude}"
                                                },
                                        createdDate = ""
                                )
                        viewModel.onSurveyClick(domainSurvey)
                },
                onShareSurvey = { survey ->
                        IntentNavigation.shareSurveyViaWhatsApp(context, survey)
                },
                onOpenSurveyMap = { survey ->
                        IntentNavigation.openSurveyInGoogleMaps(context, survey)
                },
                searchQuery = uiState.searchQuery,
                onSearchChange = viewModel::onSearchChange,
                selectedStatus =
                        when (uiState.selectedStatus) {
                                SurveyStatus.OPEN -> UiSurveyStatus.OPEN
                                SurveyStatus.VERIFIED -> UiSurveyStatus.VERIFIED
                                SurveyStatus.REJECTED -> UiSurveyStatus.REJECTED
                                null -> null
                        },
                onStatusFilterChange = { status ->
                        val domainStatus =
                                when (status) {
                                        UiSurveyStatus.OPEN -> SurveyStatus.OPEN
                                        UiSurveyStatus.VERIFIED -> SurveyStatus.VERIFIED
                                        UiSurveyStatus.REJECTED -> SurveyStatus.REJECTED
                                        null -> null
                                }
                        viewModel.onStatusFilterChange(domainStatus)
                },
                username = uiState.username,
                onNavigateToProfile = { viewModel.onNavigateToProfile() },
                onNavigateToVerification = { surveyId, locationName ->
                        viewModel.onNavigateToVerification(surveyId, locationName)
                },
                onNavigateToLogin = { viewModel.onNavigateToLogin() },
                onLogout = {
                        Log.d("DashboardRoute", "Logout called")
                        viewModel.onLogout()
                },
                surveyStats =
                        UiSurveyStats(
                                total = uiState.surveyStats.total,
                                open = uiState.surveyStats.open,
                                verified = uiState.surveyStats.verified,
                                rejected = uiState.surveyStats.rejected
                        ),
                modifier = modifier,
                onNotificationClick = {},
                onStatsClick = {}
        )
}
