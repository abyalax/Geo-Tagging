package com.app.features.auth.presentation.route

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.lifecycle.viewmodel.compose.viewModel
import com.app.core.navigation.NavigationManager
import com.app.features.auth.presentation.screen.LoginScreen
import com.app.features.auth.presentation.viewmodel.LoginViewModel
import kotlinx.coroutines.flow.collectLatest

@Composable
fun LoginRoute(
        navigationManager: NavigationManager,
        modifier: Modifier = Modifier,
        viewModel: LoginViewModel = viewModel()
) {
    val uiState by viewModel.uiState.collectAsState()

    LaunchedEffect(viewModel.navigationEvents) {
        viewModel.navigationEvents.collectLatest { event ->
            event?.let { navigationManager.navigate(it) }
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
