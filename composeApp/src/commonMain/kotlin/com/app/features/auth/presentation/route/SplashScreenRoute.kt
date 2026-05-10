package com.app.features.auth.presentation.route

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import com.app.core.domain.usecase.auth.GetSessionUseCase
import com.app.core.navigation.NavigationEvent
import com.app.core.navigation.NavigationManager
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

@Composable
fun SplashScreenRoute(navigationManager: NavigationManager, getSessionUseCase: GetSessionUseCase) {
    LaunchedEffect(Unit) {
        launch {
            getSessionUseCase().collectLatest { session ->
                if (session.isLoggedIn) {
                    navigationManager.navigate(NavigationEvent.NavigateToDashboard)
                } else {
                    navigationManager.navigate(NavigationEvent.NavigateToLogin)
                }
            }
        }
    }

    Box(
        modifier = Modifier.fillMaxSize().background(MaterialTheme.colorScheme.primary),
        contentAlignment = Alignment.Center
    ) { CircularProgressIndicator(color = MaterialTheme.colorScheme.onPrimary) }
}
