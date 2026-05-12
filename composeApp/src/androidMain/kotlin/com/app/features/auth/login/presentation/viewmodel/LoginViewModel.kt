package com.app.features.auth.login.presentation.viewmodel

import android.content.Context
import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.app.core.domain.usecase.auth.LoginUseCase
import com.app.core.navigation.AndroidNavigationManager
import com.app.core.navigation.NavigationEvent
import com.app.features.auth.login.presentation.LoginPresenter
import com.app.features.auth.login.presentation.LoginUiState
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

/** Android-specific ViewModel that wraps the platform-agnostic LoginPresenter. */
class LoginViewModel(
        private val loginUseCase: LoginUseCase,
        private val navigationManager: AndroidNavigationManager,
        private val context: Context
) : ViewModel() {

    private val presenter =
            LoginPresenter(loginUseCase = loginUseCase, navigationEmitter = navigationManager)

    // Expose presenter state to UI
    val uiState: StateFlow<LoginUiState> = presenter.uiState

    // Track navigation separately for Route handling
    private val _navigationEvents = MutableStateFlow<NavigationEvent?>(null)
    val navigationEvents: StateFlow<NavigationEvent?> = _navigationEvents.asStateFlow()

    fun onUsernameChange(username: String) = presenter.onUsernameChange(username)

    fun onPasswordChange(password: String) = presenter.onPasswordChange(password)

    fun onLoginClick() {
        Log.d("LoginViewModel", "=== onLoginClick() called ===")
        viewModelScope.launch {
            try {
                val currentState = uiState.value
                Log.d(
                        "LoginViewModel",
                        "Username: '${currentState.username}', Password: '${currentState.password}'"
                )

                // Validation
                if (currentState.username.isBlank() || currentState.password.isBlank()) {
                    Log.d("LoginViewModel", "❌ Validation failed: empty credentials")
                    presenter.setError("Username and password required")
                    return@launch
                }

                Log.d("LoginViewModel", "✅ Validation passed, calling LoginUseCase...")
                presenter.setLoading(true)

                // Call LoginUseCase (suspend function)
                val result = loginUseCase(currentState.username, currentState.password)

                result.onSuccess { session ->
                    Log.d("LoginViewModel", "✅ Login SUCCESS! Session: $session")

                    // ✅ IMPORTANT: Sync SharedPreferences BEFORE navigating
                    try {
                        val prefs = context.getSharedPreferences("app_prefs", Context.MODE_PRIVATE)
                        prefs.edit().putBoolean("is_logged_in", true)
                                .putString("username", session.username)
                                .putString("password", session.password ?: "")
                                .commit()
                        Log.d("LoginViewModel", "✅ SharedPreferences synced")
                    } catch (e: Exception) {
                        Log.e("LoginViewModel", "❌ Failed to sync SharedPreferences: ${e.message}")
                    }

                    presenter.setLoading(false)
                    presenter.clearError()

                    Log.d("LoginViewModel", "Navigating to Dashboard...")
                    navigationManager.navigate(NavigationEvent.NavigateToDashboard)
                }

                result.onFailure { error ->
                    Log.d("LoginViewModel", "❌ Login FAILED: ${error.message}")
                    presenter.setLoading(false)
                    presenter.setError(error.message ?: "Login failed")
                }
            } catch (e: Exception) {
                Log.e("LoginViewModel", "❌ Exception: ${e.message}", e)
                presenter.setLoading(false)
                presenter.setError(e.message ?: "Unknown error")
            }
        }
    }

    fun clearError() = presenter.clearError()
}
