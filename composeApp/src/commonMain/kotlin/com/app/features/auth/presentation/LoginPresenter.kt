package com.app.features.auth.login.presentation

import com.app.core.domain.usecase.auth.LoginUseCase
import com.app.core.navigation.NavigationEvent
import com.app.core.navigation.NavigationEventEmitter
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

/** Platform-agnostic LoginPresenter - Pure business logic. */
data class LoginUiState(
        val username: String = "",
        val password: String = "",
        val isLoading: Boolean = false,
        val error: String? = null
)

class LoginPresenter(
        private val loginUseCase: LoginUseCase,
        private val navigationEmitter: NavigationEventEmitter
) {
    private val _uiState = MutableStateFlow<LoginUiState>(LoginUiState())
    val uiState: StateFlow<LoginUiState> = _uiState.asStateFlow()

    fun onUsernameChange(username: String) {
        _uiState.value = _uiState.value.copy(username = username)
    }

    fun onPasswordChange(password: String) {
        _uiState.value = _uiState.value.copy(password = password)
    }

    fun setLoading(isLoading: Boolean) {
        _uiState.value = _uiState.value.copy(isLoading = isLoading)
    }

    fun setError(error: String) {
        _uiState.value = _uiState.value.copy(error = error)
    }

    fun clearError() {
        _uiState.value = _uiState.value.copy(error = null)
    }

    /**
     * Navigate to dashboard after successful login. Called from androidMain/LoginViewModel after
     * login succeeds.
     */
    fun navigateToDashboard() {
        navigationEmitter.navigate(NavigationEvent.NavigateToDashboard)
    }
}
