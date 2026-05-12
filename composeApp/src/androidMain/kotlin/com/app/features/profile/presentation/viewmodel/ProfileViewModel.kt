package com.app.features.profile.presentation.viewmodel

import android.content.Context
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
        private val getSessionUseCase: GetSessionUseCase,
        private val context: Context
) : ViewModel() {
    private val _uiState = MutableStateFlow(ProfileUiState())
    val uiState: StateFlow<ProfileUiState> = _uiState.asStateFlow()

    private val _navigationEvents = MutableStateFlow<NavigationEvent?>(null)
    val navigationEvents: StateFlow<NavigationEvent?> = _navigationEvents.asStateFlow()

    init {
        loadProfile()
    }

    fun loadProfile() {
        viewModelScope.launch {
            try {
                val session = getSessionUseCase().first()
                val prefs = context.getSharedPreferences("app_prefs", Context.MODE_PRIVATE)
                _uiState.value =
                        ProfileUiState(
                                username = session.username ?: prefs.getString("username", "") ?: "",
                                password = prefs.getString("password", "") ?: "",
                                isLoading = false,
                                error = null
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
            _uiState.value = currentState.copy(isLoading = true)
            try {
                authRepository.updateProfile(currentState.username, currentState.password)
                    .onSuccess {
                        _uiState.value = currentState.copy(isLoading = false, error = null)
                    }
                    .onFailure { error ->
                        _uiState.value = currentState.copy(isLoading = false, error = error.message)
                    }
            } catch (e: Exception) {
                _uiState.value = currentState.copy(isLoading = false, error = e.message)
            }
        }
    }

    fun onNavigateBack() {
        _navigationEvents.value = NavigationEvent.NavigateToDashboard
    }
}
