package com.app.features.profile.presentation.route

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import com.app.core.navigation.NavigationEventEmitter
import com.app.features.profile.presentation.screen.ProfileScreen
import com.app.features.profile.presentation.viewmodel.ProfileViewModel
import kotlinx.coroutines.launch
import kotlinx.coroutines.flow.collectLatest

@Composable
fun ProfileRoute(
        navigationEventEmitter: NavigationEventEmitter,
        viewModel: ProfileViewModel,
        modifier: Modifier = Modifier
) {
    val uiState by viewModel.uiState.collectAsState()
    val scope = rememberCoroutineScope()

    LaunchedEffect(Unit) {
        viewModel.loadProfile()
    }

    LaunchedEffect(viewModel.navigationEvents) {
        viewModel.navigationEvents.collectLatest { event ->
            event?.let { navigationEventEmitter.navigate(it) }
        }
    }

    ProfileScreen(
            uiState = uiState,
            onUsernameChange = viewModel::onUsernameChange,
            onPasswordChange = viewModel::onPasswordChange,
            onSaveClick = {
                scope.launch {
                    viewModel.onSaveClick()
                    viewModel.onNavigateBack()
                }
            },
            onNavigateBack = { viewModel.onNavigateBack() },
            modifier = modifier
    )
}
