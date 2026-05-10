package com.app.features.profile.presentation.route

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.lifecycle.viewmodel.compose.viewModel
import com.app.core.navigation.NavigationManager
import com.app.features.profile.presentation.screen.ProfileScreen
import com.app.features.profile.presentation.viewmodel.ProfileViewModel
import kotlinx.coroutines.flow.collectLatest

@Composable
fun ProfileRoute(
    navigationManager: NavigationManager,
    modifier: Modifier = Modifier,
    viewModel: ProfileViewModel = viewModel()
) {
    val uiState by viewModel.uiState.collectAsState()

    LaunchedEffect(viewModel.navigationEvents) {
        viewModel.navigationEvents.collectLatest { event ->
            event?.let { navigationManager.navigate(it) }
        }
    }

    ProfileScreen(
        uiState = uiState,
        onUsernameChange = viewModel::onUsernameChange,
        onPasswordChange = viewModel::onPasswordChange,
        onSaveClick = viewModel::onSaveClick,
        onNavigateBack = viewModel::onNavigateBack,
        modifier = modifier
    )
}
