package com.app.features.verification.presentation.route

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.lifecycle.viewmodel.compose.viewModel
import com.app.core.navigation.NavigationManager
import com.app.features.verification.presentation.screen.VerificationScreen
import com.app.features.verification.presentation.viewmodel.VerificationViewModel
import kotlinx.coroutines.flow.collectLatest

@Composable
fun VerificationRoute(
        surveyId: String,
        locationName: String,
        navigationManager: NavigationManager,
        modifier: Modifier = Modifier,
        viewModel: VerificationViewModel = viewModel()
) {
    val uiState by viewModel.uiState.collectAsState()

    LaunchedEffect(viewModel.navigationEvents) {
        viewModel.navigationEvents.collectLatest { event ->
            event?.let { navigationManager.navigate(it) }
        }
    }

    VerificationScreen(
            surveyId = surveyId,
            locationName = locationName,
            uiState = uiState,
            onSuccess = viewModel::onSuccess,
            onCancel = viewModel::onCancel,
            modifier = modifier
    )
}
