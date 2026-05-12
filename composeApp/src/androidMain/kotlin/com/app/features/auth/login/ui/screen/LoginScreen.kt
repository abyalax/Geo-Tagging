package com.app.features.auth.login.ui.screen

import android.util.Log
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.app.core.theme.ApplicationTheme
import com.app.features.auth.login.ui.components.LoginForm
import com.app.features.auth.login.presentation.LoginUiState

@Composable
fun LoginScreen(
        uiState: LoginUiState,
        onUsernameChange: (String) -> Unit,
        onPasswordChange: (String) -> Unit,
        onLoginClick: () -> Unit,
        modifier: Modifier = Modifier
) {
    Log.d(
            "LoginScreen",
            "LoginScreen rendered with state: username='${uiState.username}', isLoading=${uiState.isLoading}"
    )

    Surface(modifier = modifier.fillMaxSize(), color = MaterialTheme.colorScheme.background) {
        Column(modifier = Modifier.fillMaxSize()) {
            // Main content with weight
            Column(
                    modifier = Modifier.weight(1f).fillMaxWidth().padding(16.dp),
                    verticalArrangement = Arrangement.Center,
                    horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text("Officer Login", style = MaterialTheme.typography.headlineSmall)

                LoginForm(
                        username = uiState.username,
                        onUsernameChange = onUsernameChange,
                        password = uiState.password,
                        onPasswordChange = onPasswordChange
                )

                Button(
                        onClick = {
                            Log.d("LoginScreen", "Login button clicked")
                            Log.d(
                                    "LoginScreen",
                                    "Username: '${uiState.username}', Password: '${uiState.password}'"
                            )
                            onLoginClick()
                            Log.d("LoginScreen", "onLoginClick called")
                        },
                        modifier = Modifier.fillMaxWidth().padding(top = 16.dp),
                        colors =
                                ButtonDefaults.buttonColors(
                                        containerColor = MaterialTheme.colorScheme.primary,
                                        contentColor = MaterialTheme.colorScheme.onPrimary
                                ),
                        enabled = !uiState.isLoading
                ) {
                    if (uiState.isLoading) {
                        CircularProgressIndicator(
                                modifier = Modifier.padding(end = 8.dp),
                                color = MaterialTheme.colorScheme.onPrimary
                        )
                        Text("Logging in...")
                    } else {
                        Text("Login")
                    }
                }

                // Show error message if any
                uiState.error?.let { error ->
                    Text(
                            text = error,
                            color = MaterialTheme.colorScheme.error,
                            modifier = Modifier.padding(top = 8.dp)
                    )
                }
            }

        }
    }
}

@Preview
@Composable
fun LoginScreenPreview() {
    ApplicationTheme {
        LoginScreen(
                uiState = LoginUiState(),
                onUsernameChange = {},
                onPasswordChange = {},
                onLoginClick = {}
        )
    }
}
