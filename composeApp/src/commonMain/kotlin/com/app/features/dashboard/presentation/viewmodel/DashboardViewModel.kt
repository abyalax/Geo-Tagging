package com.app.features.dashboard.presentation.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.app.core.domain.usecase.auth.GetSessionUseCase
import com.app.core.domain.usecase.auth.LogoutUseCase
import com.app.core.navigation.NavigationEvent
import com.app.features.dashboard.domain.model.DashboardUiState
import com.app.features.dashboard.domain.model.Survey
import com.app.features.dashboard.domain.model.SurveyStatus
import com.app.features.dashboard.domain.usecase.GetSurveysUseCase
import com.app.features.dashboard.domain.usecase.OpenSurveyMapUseCase
import com.app.features.dashboard.domain.usecase.ShareSurveyUseCase
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch

class DashboardViewModel(
    private val getSurveysUseCase: GetSurveysUseCase,
    private val getSessionUseCase: GetSessionUseCase,
    private val logoutUseCase: LogoutUseCase,
    private val shareSurveyUseCase: ShareSurveyUseCase,
    private val openSurveyMapUseCase: OpenSurveyMapUseCase
) : ViewModel() {

    private val _uiState = MutableStateFlow<DashboardUiState>(DashboardUiState())
    val uiState: StateFlow<DashboardUiState> = _uiState.asStateFlow()

    private val _navigationEvents = MutableStateFlow<NavigationEvent?>(null)
    val navigationEvents: StateFlow<NavigationEvent?> = _navigationEvents.asStateFlow()

    init {
        loadDashboardData()
    }

    private fun loadDashboardData() {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isLoading = true)

            try {
                val surveys = getSurveysUseCase()
                val session = getSessionUseCase().first()

                _uiState.value =
                    _uiState.value.copy(
                        surveys = surveys,
                        filteredSurveys = surveys,
                        username = session.username ?: "Field Officer",
                        isLoading = false
                    )
            } catch (e: Exception) {
                _uiState.value = _uiState.value.copy(error = e.message, isLoading = false)
            }
        }
    }

    fun onSurveyClick(survey: Survey) {
        _navigationEvents.value =
            NavigationEvent.NavigateToVerification(
                surveyId = survey.id.toString(),
                locationName = survey.title
            )
    }

    fun onShareSurvey(survey: Survey) {
        viewModelScope.launch { shareSurveyUseCase(survey) }
    }

    fun onOpenSurveyMap(survey: Survey) {
        viewModelScope.launch { openSurveyMapUseCase(survey) }
    }

    fun onSearchChange(query: String) {
        _uiState.value = _uiState.value.copy(searchQuery = query)
        filterSurveys()
    }

    fun onStatusFilterChange(status: SurveyStatus?) {
        _uiState.value = _uiState.value.copy(selectedStatus = status)
        filterSurveys()
    }

    fun onNavigateToProfile() {
        _navigationEvents.value = NavigationEvent.NavigateToProfile
    }

    fun onNavigateToVerification(surveyId: String, locationName: String) {
        _navigationEvents.value =
            NavigationEvent.NavigateToVerification(
                surveyId = surveyId,
                locationName = locationName
            )
    }

    fun onNavigateToLogin() {
        _navigationEvents.value = NavigationEvent.NavigateToLogin
    }

    fun onLogout() {
        viewModelScope.launch {
            logoutUseCase()
                .onSuccess { _navigationEvents.value = NavigationEvent.NavigateToLogin }
                .onFailure { error ->
                    _uiState.value = _uiState.value.copy(error = error.message)
                }
        }
    }

    private fun filterSurveys() {
        val currentState = _uiState.value
        val filtered =
            currentState.surveys.filter { survey ->
                val matchesSearch =
                    currentState.searchQuery.isBlank() ||
                            survey.title.contains(
                                currentState.searchQuery,
                                ignoreCase = true
                            ) ||
                            survey.description.contains(
                                currentState.searchQuery,
                                ignoreCase = true
                            )

                val matchesStatus =
                    currentState.selectedStatus == null ||
                            survey.status == currentState.selectedStatus

                matchesSearch && matchesStatus
            }

        _uiState.value = currentState.copy(filteredSurveys = filtered)
    }

    fun clearError() {
        _uiState.value = _uiState.value.copy(error = null)
    }

    fun clearNavigationEvent() {
        _navigationEvents.value = null
    }
}
