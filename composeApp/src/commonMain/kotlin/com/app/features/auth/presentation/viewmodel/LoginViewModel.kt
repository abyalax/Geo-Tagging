package com.app.features.auth.presentation.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.app.core.domain.usecase.auth.LoginUseCase
import com.app.core.navigation.NavigationEvent
import com.app.core.domain.model.UserSession
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

data class LoginUiState(
    val username: String = "",
    val password: String = "",
    val isLoading: Boolean = false,
    val error: String? = null
)

class LoginViewModel(
    private val loginUseCase: LoginUseCase
) : ViewModel() {
    
    private val _uiState = MutableStateFlow(LoginUiState())
    val uiState: StateFlow<LoginUiState> = _uiState.asStateFlow()
    
    private val _navigationEvents = MutableStateFlow<NavigationEvent?>(null)
    val navigationEvents: StateFlow<NavigationEvent?> = _navigationEvents.asStateFlow()

    fun onUsernameChange(username: String) {
        _uiState.value = _uiState.value.copy(username = username)
    }

    fun onPasswordChange(password: String) {
        _uiState.value = _uiState.value.copy(password = password)
    }

    fun onLoginClick() {
        val currentState = _uiState.value
        if (currentState.username.isBlank() || currentState.password.isBlank()) {
            _uiState.value = currentState.copy(error = "Username and password required")
            return
        }

        viewModelScope.launch {
            _uiState.value = currentState.copy(isLoading = true)
            
            try {
                loginUseCase(currentState.username, currentState.password)
                    .onSuccess {
                        _navigationEvents.value = NavigationEvent.NavigateToDashboard
                    }
                    .onFailure { error ->
                        _uiState.value = currentState.copy(
                            error = error.message,
                            isLoading = false
                        )
                    }
            } catch (e: Exception) {
                _uiState.value = currentState.copy(
                    error = e.message,
                    isLoading = false
                )
            }
        }
    }

    fun clearError() {
        _uiState.value = _uiState.value.copy(error = null)
    }

    fun clearNavigationEvent() {
        _navigationEvents.value = null
    }
}
