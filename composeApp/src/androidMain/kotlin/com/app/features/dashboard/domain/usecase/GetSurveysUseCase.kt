package com.app.features.dashboard.domain.usecase

import com.app.features.dashboard.data.SurveyRepository
import com.app.features.dashboard.domain.model.Survey
import com.app.features.dashboard.model.SurveyStatus

class GetSurveysUseCase {
    suspend operator fun invoke(): List<Survey> {
        val surveys = SurveyRepository.getAllSurveys()

        // Convert from androidMain Survey model to commonMain Survey model
        return surveys.map { survey ->
            Survey(
                    id = survey.id,
                    title = survey.title,
                    description = survey.description,
                    status =
                            when (survey.status) {
                                SurveyStatus.OPEN ->
                                        com.app.features.dashboard.domain.model.SurveyStatus.OPEN
                                SurveyStatus.VERIFIED ->
                                        com.app.features.dashboard.domain.model.SurveyStatus
                                                .VERIFIED
                                SurveyStatus.REJECTED ->
                                        com.app.features.dashboard.domain.model.SurveyStatus
                                                .REJECTED
                            },
                    latitude = survey.latitude,
                    longitude = survey.longitude,
                    location = "${survey.latitude}, ${survey.longitude}",
                    createdDate = ""
            )
        }
    }
}
