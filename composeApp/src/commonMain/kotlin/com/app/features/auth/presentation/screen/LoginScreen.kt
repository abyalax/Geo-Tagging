package com.app.features.auth.presentation.screen

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.app.features.auth.presentation.viewmodel.LoginUiState

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun LoginScreen(
        uiState: LoginUiState,
        onUsernameChange: (String) -> Unit,
        onPasswordChange: (String) -> Unit,
        onLoginClick: () -> Unit,
        modifier: Modifier = Modifier
) {
        Column(
                modifier = modifier.fillMaxSize().padding(16.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center
        ) {
                Text(text = "Login", style = MaterialTheme.typography.headlineMedium)

                Spacer(modifier = Modifier.height(32.dp))

                OutlinedTextField(
                        value = uiState.username,
                        onValueChange = onUsernameChange,
                        label = { Text("Username") },
                        modifier = Modifier.fillMaxWidth()
                )

                Spacer(modifier = Modifier.height(16.dp))

                OutlinedTextField(
                        value = uiState.password,
                        onValueChange = onPasswordChange,
                        label = { Text("Password") },
                        modifier = Modifier.fillMaxWidth()
                )

                Spacer(modifier = Modifier.height(24.dp))

                if (uiState.error != null) {
                        Text(
                                text = uiState.error,
                                color = MaterialTheme.colorScheme.error,
                                modifier = Modifier.padding(bottom = 16.dp)
                        )
                }

                Button(
                        onClick = onLoginClick,
                        enabled = !uiState.isLoading,
                        modifier = Modifier.fillMaxWidth()
                ) {
                        if (uiState.isLoading) {
                                CircularProgressIndicator(
                                        modifier = Modifier.size(20.dp),
                                        color = MaterialTheme.colorScheme.onPrimary
                                )
                        } else {
                                Text("Login")
                        }
                }
        }
}
