package com.app.features.dashboard.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.app.core.domain.usecase.auth.GetSessionUseCase
import com.app.core.domain.usecase.auth.LogoutUseCase
import com.app.core.navigation.DefaultNavigationEventEmitter
import com.app.features.dashboard.domain.usecase.GetSurveysUseCase
import com.app.features.dashboard.domain.usecase.OpenSurveyMapUseCase
import com.app.features.dashboard.domain.usecase.ShareSurveyUseCase
import com.app.features.dashboard.presentation.DashboardPresenter
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

/**
 * Android-specific ViewModel that wraps the platform-agnostic DashboardPresenter. Provides Android
 * lifecycle management and bridges to Compose UI.
 *
 * Location: androidMain/features/dashboard/presentation/viewmodel/DashboardViewModel.kt
 */
class DashboardViewModel(
        getSurveysUseCase: GetSurveysUseCase,
        getSessionUseCase: GetSessionUseCase,
        logoutUseCase: LogoutUseCase,
        shareSurveyUseCase: ShareSurveyUseCase,
        openSurveyMapUseCase: OpenSurveyMapUseCase
) : ViewModel() {

    private val navigationEmitter = DefaultNavigationEventEmitter()
    private val presenter =
            DashboardPresenter(
                    getSurveysUseCase = getSurveysUseCase,
                    getSessionUseCase = getSessionUseCase,
                    logoutUseCase = logoutUseCase,
                    shareSurveyUseCase = shareSurveyUseCase,
                    openSurveyMapUseCase = openSurveyMapUseCase,
                    navigationEmitter = navigationEmitter
            )

    // Expose presenter state to UI
    val uiState: StateFlow<com.app.features.dashboard.domain.model.DashboardUiState> =
            presenter.uiState
    val navigationEvents = navigationEmitter.navigationEvents

    init {
        loadDashboardData()
    }

    fun loadDashboardData() {
        viewModelScope.launch { presenter.loadDashboardData() }
    }

    fun onSurveyClick(survey: com.app.features.dashboard.domain.model.Survey) =
            presenter.onSurveyClick(survey)

    fun onShareSurvey(survey: com.app.features.dashboard.domain.model.Survey) {
        viewModelScope.launch { presenter.onShareSurvey(survey) }
    }

    fun onOpenSurveyMap(survey: com.app.features.dashboard.domain.model.Survey) {
        viewModelScope.launch { presenter.onOpenSurveyMap(survey) }
    }

    fun onSearchChange(query: String) = presenter.onSearchChange(query)
    fun onStatusFilterChange(status: com.app.features.dashboard.domain.model.SurveyStatus?) =
            presenter.onStatusFilterChange(status)
    fun onNavigateToProfile() = presenter.onNavigateToProfile()
    fun onNavigateToVerification(surveyId: String, locationName: String) =
            presenter.onNavigateToVerification(surveyId, locationName)
    fun onNavigateToLogin() = presenter.onNavigateToLogin()

    fun onLogout() {
        viewModelScope.launch { presenter.onLogout() }
    }

    fun clearError() = presenter.clearError()

    fun refresh() = loadDashboardData()
}
