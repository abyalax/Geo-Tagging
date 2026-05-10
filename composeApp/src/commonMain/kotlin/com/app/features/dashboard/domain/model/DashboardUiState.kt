package com.app.features.dashboard.domain.model

data class DashboardUiState(
    val surveys: List<Survey> = emptyList(),
    val filteredSurveys: List<Survey> = emptyList(),
    val searchQuery: String = "",
    val selectedStatus: SurveyStatus? = null,
    val username: String = "Field Officer",
    val isLoading: Boolean = false,
    val error: String? = null,
    val surveyStats: SurveyStats = SurveyStats(0, 0, 0, 0)
)
