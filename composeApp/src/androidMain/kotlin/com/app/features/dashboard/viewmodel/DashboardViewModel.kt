package com.app.features.dashboard.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.app.features.dashboard.data.SurveyRepository
import com.app.features.dashboard.model.Survey
import com.app.features.dashboard.model.SurveyStatus
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class DashboardViewModel : ViewModel() {
    // State management for surveys
    private val _surveyList = MutableStateFlow<List<Survey>>(emptyList())
    val surveyList: StateFlow<List<Survey>> = _surveyList.asStateFlow()

    private val _selectedSurvey = MutableStateFlow<Survey?>(null)
    val selectedSurvey: StateFlow<Survey?> = _selectedSurvey.asStateFlow()

    private val _filteredSurveys = MutableStateFlow<List<Survey>>(emptyList())
    val filteredSurveys: StateFlow<List<Survey>> = _filteredSurveys.asStateFlow()

    private val _searchQuery = MutableStateFlow("")
    val searchQuery: StateFlow<String> = _searchQuery.asStateFlow()

    private val _selectedStatusFilter = MutableStateFlow<SurveyStatus?>(null)
    val selectedStatusFilter: StateFlow<SurveyStatus?> = _selectedStatusFilter.asStateFlow()

    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading.asStateFlow()

    private val _errorMessage = MutableStateFlow<String?>(null)
    val errorMessage: StateFlow<String?> = _errorMessage.asStateFlow()

    init {
        loadSurveys()
    }

    // Load all surveys from repository
    fun loadSurveys() {
        viewModelScope.launch {
            _isLoading.value = true
            try {
                val surveys = SurveyRepository.getAllSurveys()
                _surveyList.value = surveys
                _filteredSurveys.value = surveys
            } catch (e: Exception) {
                _errorMessage.value = "Error loading surveys: ${e.message}"
            } finally {
                _isLoading.value = false
            }
        }
    }

    // Select a survey
    fun selectSurvey(survey: Survey) {
        _selectedSurvey.value = survey
    }

    // Clear selection
    fun clearSelection() {
        _selectedSurvey.value = null
    }

    // Update survey status
    fun updateSurveyStatus(surveyId: Int, newStatus: SurveyStatus) {
        viewModelScope.launch {
            try {
                val success = SurveyRepository.updateSurveyStatus(surveyId, newStatus)
                if (success) {
                    // Reload surveys to reflect changes
                    loadSurveys()
                    clearSelection()
                } else {
                    _errorMessage.value = "Failed to update survey status"
                }
            } catch (e: Exception) {
                _errorMessage.value = "Error updating status: ${e.message}"
            }
        }
    }

    // Search surveys by title or description
    fun searchSurveys(query: String) {
        _searchQuery.value = query
        viewModelScope.launch {
            val allSurveys = SurveyRepository.getAllSurveys()
            _filteredSurveys.value = if (query.isBlank()) {
                allSurveys
            } else {
                allSurveys.filter {
                    it.title.contains(query, ignoreCase = true) ||
                            it.description.contains(query, ignoreCase = true)
                }
            }
        }
    }

    // Filter surveys by status
    fun filterByStatus(status: SurveyStatus?) {
        _selectedStatusFilter.value = status
        viewModelScope.launch {
            val allSurveys = SurveyRepository.getAllSurveys()
            _filteredSurveys.value = if (status == null) {
                allSurveys
            } else {
                allSurveys.filter { it.status == status }
            }
        }
    }

    // Get statistics
    fun getSurveyStats(): SurveyStats {
        val surveys = _surveyList.value
        return SurveyStats(
            total = surveys.size,
            open = surveys.count { it.status == SurveyStatus.OPEN },
            verified = surveys.count { it.status == SurveyStatus.VERIFIED },
            rejected = surveys.count { it.status == SurveyStatus.REJECTED }
        )
    }

    // Clear error message
    fun clearError() {
        _errorMessage.value = null
    }
}

data class SurveyStats(
    val total: Int = 0,
    val open: Int = 0,
    val verified: Int = 0,
    val rejected: Int = 0
)