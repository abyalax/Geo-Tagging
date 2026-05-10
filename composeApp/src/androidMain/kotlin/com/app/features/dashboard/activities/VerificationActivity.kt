package com.app.features.dashboard.activities

import android.content.Intent
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import com.app.core.common.Constants
import com.app.core.navigation.AuthMiddleware
import com.app.core.navigation.ImplicitIntents
import com.app.core.theme.ApplicationTheme
import com.app.features.dashboard.data.SurveyRepository
import com.app.features.dashboard.ui.screen.VerificationScreen
import com.app.features.profile.activities.ProfileActivity

class VerificationActivity : ComponentActivity() {
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

        val surveyId = intent.getIntExtra(Constants.EXTRA_SURVEY_ID, -1)
        val survey = SurveyRepository.getSurveyById(surveyId)
        val locationName =
                survey?.title ?: intent.getStringExtra(Constants.EXTRA_LOCATION_NAME) ?: ""

        setContent {
            ApplicationTheme {
                VerificationScreen(
                        locationName = locationName,
                        username = username,
                        onSuccess = { status ->
                            if (surveyId != -1) {
                                // Update status in repository
                                SurveyRepository.updateSurveyStatus(surveyId, status)
                            }

                            // Set result and finish activity
                            val resultIntent =
                                    Intent().apply {
                                        putExtra(Constants.EXTRA_VERIFICATION_STATUS, status.name)
                                        putExtra(Constants.EXTRA_SURVEY_ID, surveyId)
                                    }
                            setResult(RESULT_OK, resultIntent)
                            finish()
                        },
                        onCancel = {
                            setResult(RESULT_CANCELED)
                            finish()
                        },
                        onShare = {
                            survey?.let {
                                ImplicitIntents.shareToWhatsApp(
                                        context = this,
                                        title = it.title,
                                        description = it.description,
                                        latitude = it.latitude,
                                        longitude = it.longitude
                                )
                            }
                        },
                        onOpenMap = {
                            survey?.let {
                                ImplicitIntents.openMaps(
                                        context = this,
                                        latitude = it.latitude,
                                        longitude = it.longitude
                                )
                            }
                        },
                        onNavigateToHome = {
                            startActivity(Intent(this, DashboardActivity::class.java))
                            finish()
                        },
                        onNavigateToProfile = {
                            startActivity(Intent(this, ProfileActivity::class.java))
                            finish()
                        },
                        onNavigateToLogin = { AuthMiddleware.logout(this) }
                )
            }
        }
    }
}
