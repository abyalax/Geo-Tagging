package com.app.features.dashboard.domain.usecase

import com.app.features.dashboard.domain.model.Survey

class GetSurveysUseCase {
    suspend operator fun invoke(): List<Survey> {
        // TODO: Implement actual data fetching from repository
        return emptyList()
    }
}
