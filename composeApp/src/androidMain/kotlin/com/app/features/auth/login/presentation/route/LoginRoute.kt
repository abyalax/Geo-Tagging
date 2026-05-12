package com.app.features.auth.login.presentation.route

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import com.app.core.navigation.NavigationEventEmitter
import com.app.features.auth.login.ui.screen.LoginScreen
import com.app.features.auth.login.presentation.viewmodel.LoginViewModel
import kotlinx.coroutines.flow.collectLatest

/** LoginRoute - Composable route for Login screen. */
@Composable
fun LoginRoute(
        navigationEventEmitter: NavigationEventEmitter,
        modifier: Modifier = Modifier,
        viewModel: LoginViewModel
) {
        val uiState by viewModel.uiState.collectAsState()

        // ViewModel emits NavigateToDashboard after successful login
        LaunchedEffect(viewModel.navigationEvents) {
                viewModel.navigationEvents.collectLatest { event ->
                        event?.let {
                                // This is how AppNavHost hears about it
                                navigationEventEmitter.navigate(it)
                        }
                }
        }

        LoginScreen(
                uiState = uiState,
                onUsernameChange = viewModel::onUsernameChange,
                onPasswordChange = viewModel::onPasswordChange,
                onLoginClick = viewModel::onLoginClick,
                modifier = modifier
        )
}
