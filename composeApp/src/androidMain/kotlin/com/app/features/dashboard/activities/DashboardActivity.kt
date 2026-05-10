package com.app.features.dashboard.activities

import android.content.Intent
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.viewModels
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import com.app.core.common.Constants
import com.app.core.navigation.AuthMiddleware
import com.app.core.navigation.ImplicitIntents
import com.app.core.theme.ApplicationTheme
import com.app.features.dashboard.ui.screen.DashboardScreen
import com.app.features.dashboard.viewmodel.DashboardViewModel
import com.app.features.profile.activities.ProfileActivity

class DashboardActivity : ComponentActivity() {

    private val viewModel: DashboardViewModel by viewModels()

    private val verificationLauncher =
            registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
                if (result.resultCode == RESULT_OK) {
                    viewModel.loadSurveys()
                }
            }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // Check authentication - redirect to login if not authenticated
        if (!AuthMiddleware.requireAuth(this)) {
            return // Activity will be finished by middleware
        }

        // Get username from middleware (must be authenticated)
        val username =
                AuthMiddleware.getUsername(this)
                        ?: run {
                            // This should not happen if requireAuth passed, but as safety net
                            AuthMiddleware.logout(this)
                            return
                        }

        // Debug logging
        android.util.Log.d("DashboardActivity", "Authenticated user: $username")
        android.util.Log.d("DashboardActivity", "Intent extras: ${intent.extras}")

        setContent {
            val surveys by viewModel.filteredSurveys.collectAsState()
            val isLoading by viewModel.isLoading.collectAsState()
            val searchQuery by viewModel.searchQuery.collectAsState()
            val selectedStatus by viewModel.selectedStatusFilter.collectAsState()
            val surveyStats = viewModel.getSurveyStats()

            ApplicationTheme {
                DashboardScreen(
                        surveys = surveys,
                        isLoading = isLoading,
                        onSurveyClick = { survey ->
                            val intent =
                                    Intent(this, VerificationActivity::class.java).apply {
                                        putExtra(Constants.EXTRA_SURVEY_ID, survey.id)
                                        putExtra(Constants.EXTRA_LOCATION_NAME, survey.title)
                                    }
                            verificationLauncher.launch(intent)
                        },
                        onShareSurvey = { survey ->
                            ImplicitIntents.shareToWhatsApp(
                                    context = this,
                                    title = survey.title,
                                    description = survey.description,
                                    latitude = survey.latitude,
                                    longitude = survey.longitude
                            )
                        },
                        onOpenSurveyMap = { survey ->
                            ImplicitIntents.openMaps(
                                    context = this,
                                    latitude = survey.latitude,
                                    longitude = survey.longitude
                            )
                        },
                        searchQuery = searchQuery,
                        onSearchChange = viewModel::searchSurveys,
                        selectedStatus = selectedStatus,
                        onStatusFilterChange = viewModel::filterByStatus,
                        username = username,
                        onProfileClick = {
                            startActivity(Intent(this, ProfileActivity::class.java))
                        },
                        onNavigateToLogin = { AuthMiddleware.logout(this) },
                        onNotificationClick = {
                            // Handle notification click
                        },
                        onStatsClick = {
                            // Handle stats click
                        },
                        onLogout = { AuthMiddleware.logout(this) },
                        surveyStats = surveyStats
                )
            }
        }
    }
}
