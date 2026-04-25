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
import com.app.core.navigation.ImplicitIntentHelper
import com.app.core.theme.ApplicationTheme
import com.app.features.dashboard.ui.screen.DashboardScreen
import com.app.features.dashboard.viewmodel.DashboardViewModel

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

        val sensorName = intent.getStringExtra(Constants.EXTRA_SENSOR_NAME) 
            ?: Constants.DEFAULT_SENSOR_NAME
        val latitude = intent.getStringExtra(Constants.EXTRA_LATITUDE) 
            ?: Constants.DEFAULT_LATITUDE
        val longitude = intent.getStringExtra(Constants.EXTRA_LONGITUDE) 
            ?: Constants.DEFAULT_LONGITUDE

        setContent {
            val surveys by viewModel.filteredSurveys.collectAsState()
            val isLoading by viewModel.isLoading.collectAsState()
            val searchQuery by viewModel.searchQuery.collectAsState()
            val selectedStatus by viewModel.selectedStatusFilter.collectAsState()

            ApplicationTheme {
                DashboardScreen(
                    surveys = surveys,
                    isLoading = isLoading,
                    onSurveyClick = { survey ->
                        val intent = Intent(this, VerificationActivity::class.java).apply {
                            putExtra(Constants.EXTRA_SURVEY_ID, survey.id)
                            putExtra(Constants.EXTRA_SENSOR_NAME, survey.title)
                        }
                        verificationLauncher.launch(intent)
                    },
                    onViewMap = {
                        ImplicitIntentHelper.openMaps(
                            context = this,
                            latitude = latitude.toDoubleOrNull() ?: 0.0,
                            longitude = longitude.toDoubleOrNull() ?: 0.0
                        )
                    },
                    onShareSurvey = { survey ->
                        ImplicitIntentHelper.shareToWhatsApp(
                            context = this,
                            title = survey.title,
                            description = survey.description,
                            latitude = survey.latitude,
                            longitude = survey.longitude
                        )
                    },
                    onOpenSurveyMap = { survey ->
                        ImplicitIntentHelper.openMaps(
                            context = this,
                            latitude = survey.latitude,
                            longitude = survey.longitude
                        )
                    },
                    searchQuery = searchQuery,
                    onSearchChange = viewModel::searchSurveys,
                    selectedStatus = selectedStatus,
                    onStatusFilterChange = viewModel::filterByStatus,
                    sensorName = sensorName,
                    latitude = latitude,
                    longitude = longitude
                )
            }
        }
    }
}
