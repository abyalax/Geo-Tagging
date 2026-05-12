package com.app.features.profile.presentation.screen

import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.foundation.background
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.unit.dp
import com.app.features.profile.presentation.viewmodel.ProfileUiState
import com.app.ui.components.BottomNavItem
import com.app.ui.components.BottomNavigationBar
import com.app.ui.components.TopAppBar

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ProfileScreen(
        uiState: ProfileUiState,
        onUsernameChange: (String) -> Unit,
        onPasswordChange: (String) -> Unit,
        onSaveClick: () -> Unit,
        onNavigateBack: () -> Unit,
        modifier: Modifier = Modifier
) {
    val scrollState = rememberScrollState()

    Surface(modifier = modifier.fillMaxSize(), color = MaterialTheme.colorScheme.background) {
        Column(modifier = Modifier.fillMaxSize()) {
            TopAppBar(
                    username = uiState.username,
                    onProfileClick = {},
                    onNotificationClick = {},
                    onStatsClick = {},
                    onLogout = onNavigateBack
            )

            Column(
                    modifier =
                            Modifier.weight(1f)
                                    .fillMaxSize()
                                    .horizontalScroll(scrollState)
                                    .verticalScroll(scrollState)
                                    .padding(24.dp),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.spacedBy(18.dp)
            ) {
                Box(
                        modifier =
                                Modifier.size(124.dp)
                                        .clip(CircleShape)
                                        .background(MaterialTheme.colorScheme.primary),
                        contentAlignment = Alignment.Center
                ) {
                    Text(
                            text = uiState.username.take(1).uppercase(),
                            style = MaterialTheme.typography.displaySmall,
                            color = MaterialTheme.colorScheme.onPrimary
                    )
                }

                Text(text = "Profile", style = MaterialTheme.typography.headlineMedium)

                OutlinedTextField(
                        value = uiState.username,
                        onValueChange = onUsernameChange,
                        label = { Text("Username") },
                        modifier = Modifier.fillMaxWidth()
                )

                OutlinedTextField(
                        value = uiState.password,
                        onValueChange = onPasswordChange,
                        label = { Text("Password") },
                        modifier = Modifier.fillMaxWidth()
                )

                if (uiState.error != null) {
                    Text(
                            text = uiState.error,
                            color = MaterialTheme.colorScheme.error,
                            modifier = Modifier.padding(bottom = 4.dp)
                    )
                }

                Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Button(
                            onClick = onSaveClick,
                            enabled = !uiState.isLoading,
                            modifier = Modifier.weight(1f),
                            colors =
                                    ButtonDefaults.buttonColors(
                                            containerColor = MaterialTheme.colorScheme.primary
                                    )
                    ) {
                        if (uiState.isLoading) {
                            CircularProgressIndicator(
                                    modifier = Modifier.size(20.dp),
                                    color = MaterialTheme.colorScheme.onPrimary
                            )
                        } else {
                            Text("Save")
                        }
                    }

                    Button(
                            onClick = onNavigateBack,
                            modifier = Modifier.weight(1f),
                            colors =
                                    ButtonDefaults.buttonColors(
                                            containerColor = MaterialTheme.colorScheme.secondary
                                    )
                    ) {
                        Text("Back")
                    }
                }
            }

            BottomNavigationBar(
                    selectedItem = BottomNavItem.Profile,
                    onItemSelected = { item ->
                        when (item) {
                            BottomNavItem.Login, BottomNavItem.Home -> onNavigateBack()
                            BottomNavItem.Profile -> Unit
                        }
                    }
            )
        }
    }
}
