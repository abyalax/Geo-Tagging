package com.app.features.dashboard.activities

import android.content.Intent
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import com.app.core.common.Constants
import com.app.core.navigation.ImplicitIntentHelper
import com.app.core.theme.ApplicationTheme
import com.app.features.dashboard.data.SurveyRepository
import com.app.features.dashboard.ui.screen.VerificationScreen

class VerificationActivity : ComponentActivity() {
  override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)
    
    val surveyId = intent.getIntExtra(Constants.EXTRA_SURVEY_ID, -1)
    val survey = SurveyRepository.getSurveyById(surveyId)
    val sensorName = survey?.title ?: intent.getStringExtra(Constants.EXTRA_SENSOR_NAME) ?: ""

    setContent {
      ApplicationTheme {
          VerificationScreen(
                  sensorName = sensorName,
                  onSuccess = { status ->
                    if (surveyId != -1) {
                        // Update status in repository
                        SurveyRepository.updateSurveyStatus(surveyId, status)
                    }
                    
                    // Set result and finish activity
                    val resultIntent = Intent().apply { 
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
                          ImplicitIntentHelper.shareToWhatsApp(
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
                          ImplicitIntentHelper.openMaps(
                              context = this,
                              latitude = it.latitude,
                              longitude = it.longitude
                          )
                      }
                  }
          )
      }
    }
  }
}
