package com.app.features.dashboard.ui.screen

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.app.features.dashboard.data.SurveyRepository
import com.app.features.dashboard.model.SurveyStatus
import com.app.ui.components.BottomNavItem
import com.app.ui.components.BottomNavigationBar
import com.app.ui.components.TopAppBar

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun VerificationScreen(
    surveyId: String,
    locationName: String,
    onSuccess: (SurveyStatus) -> Unit = {},
    onCancel: () -> Unit = {},
    username: String = "",
    onNavigateToHome: () -> Unit = {},
    navController: NavController,
    onNavigateToLogin: () -> Unit = {
        navController.navigate("login") { popUpTo("login") { inclusive = true } }
    },
    onNavigateToProfile: (navController: NavController) -> Unit = {
        navController.navigate("profile")
    },
) {
    val context = LocalContext.current
    val actualUsername =
        if (username.isEmpty()) {
            context.getSharedPreferences("app_prefs", android.content.Context.MODE_PRIVATE)
                .getString("username", "") ?: ""
        } else {
            username
        }

    var isVerifying by remember { mutableStateOf(false) }

    Surface(modifier = Modifier.fillMaxSize(), color = MaterialTheme.colorScheme.background) {
        Column(modifier = Modifier.fillMaxSize()) {
            // Top App Bar
            TopAppBar(
                username = actualUsername,
                onProfileClick = { onNavigateToProfile(navController) },
                onNotificationClick = { /* Handle notification click */ },
                onStatsClick = { /* Handle stats click */ },
                onLogout = { onNavigateToLogin() }
            )

            // Main content with scroll
            Column(
                modifier =
                    Modifier
                        .fillMaxWidth()
                        .weight(1f)
                        .verticalScroll(rememberScrollState())
                        .padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Column(
                    modifier = Modifier.fillMaxWidth(0.85f),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text(
                        text = "Verifikasi Survey",
                        style = MaterialTheme.typography.headlineMedium,
                        modifier = Modifier.padding(bottom = 8.dp)
                    )
                    Text(
                        text = locationName,
                        style = MaterialTheme.typography.bodyLarge,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        modifier = Modifier.padding(bottom = 24.dp)
                    )

                    // Action buttons
                    Column(
                        modifier = Modifier.fillMaxWidth(),
                        verticalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Button(
                            onClick = {
                                isVerifying = true
                                val surveyIdInt = surveyId.toIntOrNull() ?: -1
                                if (surveyIdInt != -1) {
                                    SurveyRepository.updateSurveyStatus(
                                        surveyIdInt,
                                        SurveyStatus.VERIFIED
                                    )
                                }
                                onSuccess(SurveyStatus.VERIFIED)
                                onNavigateToHome()
                            },
                            modifier = Modifier.fillMaxWidth(),
                            enabled = !isVerifying,
                            colors =
                                ButtonDefaults.buttonColors(
                                    containerColor = MaterialTheme.colorScheme.primary
                                )
                        ) { Text("Setujui (Verified)") }

                        Button(
                            onClick = {
                                isVerifying = true
                                val surveyIdInt = surveyId.toIntOrNull() ?: -1
                                if (surveyIdInt != -1) {
                                    SurveyRepository.updateSurveyStatus(
                                        surveyIdInt,
                                        SurveyStatus.REJECTED
                                    )
                                }
                                onSuccess(SurveyStatus.REJECTED)
                                onNavigateToHome()
                            },
                            modifier = Modifier.fillMaxWidth(),
                            enabled = !isVerifying,
                            colors =
                                ButtonDefaults.buttonColors(
                                    containerColor = MaterialTheme.colorScheme.error
                                )
                        ) { Text("Tolak (Rejected)") }

                        OutlinedButton(
                            onClick = onCancel,
                            modifier = Modifier.fillMaxWidth(),
                            enabled = !isVerifying
                        ) { Text("Batal") }
                    }
                }
            }

            // Bottom Navigation
            BottomNavigationBar(
                selectedItem = BottomNavItem.Home,
                onItemSelected = { item ->
                    when (item) {
                        BottomNavItem.Login -> {
                            onNavigateToLogin()
                        }

                        BottomNavItem.Home -> {
                            onNavigateToHome()
                        }

                        BottomNavItem.Profile -> {
                            onNavigateToProfile(navController)
                        }
                    }
                }
            )
        }
    }
}
