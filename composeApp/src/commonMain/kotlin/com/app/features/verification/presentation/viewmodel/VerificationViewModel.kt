package com.app.features.verification.presentation.viewmodel

import androidx.lifecycle.ViewModel
import com.app.core.navigation.NavigationEvent
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

data class VerificationUiState(
    val isLoading: Boolean = false,
    val error: String? = null,
    val isVerified: Boolean = false
)

class VerificationViewModel : ViewModel() {

    private val _uiState = MutableStateFlow(VerificationUiState())
    val uiState: StateFlow<VerificationUiState> = _uiState.asStateFlow()

    private val _navigationEvents = MutableStateFlow<NavigationEvent?>(null)
    val navigationEvents: StateFlow<NavigationEvent?> = _navigationEvents.asStateFlow()

    fun onSuccess() {
        _navigationEvents.value = NavigationEvent.NavigateToDashboard
    }

    fun onCancel() {
        _navigationEvents.value = NavigationEvent.NavigateBack
    }

    fun clearError() {
        _uiState.value = _uiState.value.copy(error = null)
    }

    fun clearNavigationEvent() {
        _navigationEvents.value = null
    }
}
