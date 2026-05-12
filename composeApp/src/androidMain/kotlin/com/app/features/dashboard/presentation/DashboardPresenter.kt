package com.app.features.dashboard.presentation

import com.app.core.domain.usecase.auth.GetSessionUseCase
import com.app.core.domain.usecase.auth.LogoutUseCase
import com.app.core.navigation.NavigationEvent
import com.app.core.navigation.NavigationEventEmitter
import com.app.features.dashboard.domain.model.DashboardUiState
import com.app.features.dashboard.domain.model.Survey
import com.app.features.dashboard.domain.model.SurveyStats
import com.app.features.dashboard.domain.model.SurveyStatus
import com.app.features.dashboard.domain.usecase.GetSurveysUseCase
import com.app.features.dashboard.domain.usecase.OpenSurveyMapUseCase
import com.app.features.dashboard.domain.usecase.ShareSurveyUseCase
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.first

/**
 * Platform-agnostic presenter for Dashboard. Pure business logic, no Android/Compose imports. No
 * ViewModel, no lifecycle awareness.
 *
 * Location: androidMain/features/dashboard/presentation/DashboardPresenter.kt
 *
 * Usage pattern:
 * - androidMain wraps this in a ViewModel
 * - iOS wraps this in @StateObject
 * - Unit tests call methods directly
 */
class DashboardPresenter(
        private val getSurveysUseCase: GetSurveysUseCase,
        private val getSessionUseCase: GetSessionUseCase,
        private val logoutUseCase: LogoutUseCase,
        private val shareSurveyUseCase: ShareSurveyUseCase,
        private val openSurveyMapUseCase: OpenSurveyMapUseCase,
        private val navigationEmitter: NavigationEventEmitter
) {
    private val _uiState = MutableStateFlow<DashboardUiState>(DashboardUiState())
    val uiState: StateFlow<DashboardUiState> = _uiState.asStateFlow()

    /** Load dashboard data. Called by ViewModel. Caller must provide coroutine scope. */
    suspend fun loadDashboardData() {
        _uiState.value = _uiState.value.copy(isLoading = true)

        try {
            val surveys = getSurveysUseCase()
            val session = getSessionUseCase().first()
            val surveyStats = calculateSurveyStats(surveys)

            _uiState.value =
                    _uiState.value.copy(
                            surveys = surveys,
                            username = session.username.orEmpty(),
                            surveyStats = surveyStats,
                            isLoading = false
                    )
        } catch (e: Exception) {
            _uiState.value = _uiState.value.copy(error = e.message, isLoading = false)
        }
    }

    fun onSurveyClick(survey: Survey) {
        navigationEmitter.navigate(
                NavigationEvent.NavigateToVerification(
                        surveyId = survey.id.toString(),
                        locationName = survey.title
                )
        )
    }

    suspend fun onShareSurvey(survey: Survey) {
        shareSurveyUseCase(survey)
    }

    suspend fun onOpenSurveyMap(survey: Survey) {
        openSurveyMapUseCase(survey)
    }

    fun onSearchChange(query: String) {
        val current = _uiState.value
        val filtered = applyFilters(current.surveys, query, current.selectedStatus)
        _uiState.value = current.copy(searchQuery = query, filteredSurveys = filtered)
    }

    fun onStatusFilterChange(status: SurveyStatus?) {
        val current = _uiState.value
        val filtered = applyFilters(current.surveys, current.searchQuery, status)
        _uiState.value = current.copy(selectedStatus = status, filteredSurveys = filtered)
    }

    fun onNavigateToProfile() {
        navigationEmitter.navigate(NavigationEvent.NavigateToProfile)
    }

    fun onNavigateToVerification(surveyId: String, locationName: String) {
        navigationEmitter.navigate(
                NavigationEvent.NavigateToVerification(
                        surveyId = surveyId,
                        locationName = locationName
                )
        )
    }

    fun onNavigateToLogin() {
        navigationEmitter.navigate(NavigationEvent.NavigateToLogin)
    }

    suspend fun onLogout() {
        logoutUseCase()
                .onSuccess { navigationEmitter.navigate(NavigationEvent.NavigateToLogin) }
                .onFailure { error -> _uiState.value = _uiState.value.copy(error = error.message) }
    }

    private fun applyFilters(
            surveys: List<Survey>,
            query: String,
            status: SurveyStatus?
    ): List<Survey> {
        return surveys.filter { survey ->
            val matchesSearch =
                    query.isBlank() ||
                            survey.title.contains(query, ignoreCase = true) ||
                            survey.description.contains(query, ignoreCase = true)
            val matchesStatus = status == null || survey.status == status
            matchesSearch && matchesStatus
        }
    }

    private fun calculateSurveyStats(surveys: List<Survey>): SurveyStats {
        val total = surveys.size
        val open = surveys.count { it.status == SurveyStatus.OPEN }
        val verified = surveys.count { it.status == SurveyStatus.VERIFIED }
        val rejected = surveys.count { it.status == SurveyStatus.REJECTED }

        return SurveyStats(total = total, open = open, verified = verified, rejected = rejected)
    }

    fun clearError() {
        _uiState.value = _uiState.value.copy(error = null)
    }
}
