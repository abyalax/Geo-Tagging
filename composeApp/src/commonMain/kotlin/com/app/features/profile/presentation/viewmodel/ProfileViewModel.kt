package com.app.features.profile.presentation.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.app.core.data.repository.AuthRepository
import com.app.core.domain.usecase.auth.GetSessionUseCase
import com.app.core.navigation.NavigationEvent
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch

data class ProfileUiState(
    val username: String = "",
    val password: String = "",
    val isLoading: Boolean = false,
    val error: String? = null
)

class ProfileViewModel(
    private val authRepository: AuthRepository,
    private val getSessionUseCase: GetSessionUseCase
) : ViewModel() {

    private val _uiState = MutableStateFlow(ProfileUiState())
    val uiState: StateFlow<ProfileUiState> = _uiState.asStateFlow()

    private val _navigationEvents = MutableStateFlow<NavigationEvent?>(null)
    val navigationEvents: StateFlow<NavigationEvent?> = _navigationEvents.asStateFlow()

    init {
        loadProfile()
    }

    private fun loadProfile() {
        viewModelScope.launch {
            try {
                val session = getSessionUseCase().first()
                _uiState.value =
                    _uiState.value.copy(
                        username = session.username ?: "",
                        password = session.password ?: ""
                    )
            } catch (e: Exception) {
                _uiState.value = _uiState.value.copy(error = e.message)
            }
        }
    }

    fun onUsernameChange(username: String) {
        _uiState.value = _uiState.value.copy(username = username)
    }

    fun onPasswordChange(password: String) {
        _uiState.value = _uiState.value.copy(password = password)
    }

    fun onSaveClick() {
        val currentState = _uiState.value
        viewModelScope.launch {
            try {
                authRepository
                    .updateProfile(currentState.username, currentState.password)
                    .onSuccess { _uiState.value = currentState.copy(error = null) }
                    .onFailure { error ->
                        _uiState.value = currentState.copy(error = error.message)
                    }
            } catch (e: Exception) {
                _uiState.value = currentState.copy(error = e.message)
            }
        }
    }

    fun onNavigateBack() {
        _navigationEvents.value = NavigationEvent.NavigateBack
    }

    fun clearError() {
        _uiState.value = _uiState.value.copy(error = null)
    }

    fun clearNavigationEvent() {
        _navigationEvents.value = null
    }
}
