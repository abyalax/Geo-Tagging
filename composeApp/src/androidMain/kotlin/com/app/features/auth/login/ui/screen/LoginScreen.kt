package com.app.features.auth.login.ui.screen

import android.util.Log
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.app.core.theme.ApplicationTheme
import com.app.features.auth.login.ui.components.LoginForm
import com.app.ui.components.BottomNavItem
import com.app.ui.components.BottomNavigationBar

@Composable
fun LoginScreen(onNavigateToDashboard: (String, String) -> Unit) {
    var username by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }

    Surface(modifier = Modifier.fillMaxSize(), color = MaterialTheme.colorScheme.background) {
        Column(modifier = Modifier.fillMaxSize()) {
            // Main content with weight
            Column(
                    modifier = Modifier.weight(1f).fillMaxWidth().padding(16.dp),
                    verticalArrangement = Arrangement.Center,
                    horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text("Officer Login", style = MaterialTheme.typography.headlineSmall)

                LoginForm(
                        username = username,
                        onUsernameChange = { username = it },
                        password = password,
                        onPasswordChange = { password = it }
                )

                Button(
                        onClick = {
                            Log.d("LoginScreen", "Login button clicked")
                            Log.d("LoginScreen", "Username: '$username', Password: '$password'")
                            // Simple validation
                            if (username.isNotEmpty() && password.isNotEmpty()) {
                                Log.d(
                                        "LoginScreen",
                                        "Validation passed, calling onNavigateToDashboard"
                                )
                                onNavigateToDashboard(username, password)
                            } else {
                                Log.d(
                                        "LoginScreen",
                                        "Validation failed - empty username or password"
                                )
                            }
                        },
                        modifier = Modifier.fillMaxWidth().padding(top = 16.dp),
                        colors =
                                ButtonDefaults.buttonColors(
                                        containerColor = MaterialTheme.colorScheme.primary,
                                        contentColor = MaterialTheme.colorScheme.onPrimary
                                )
                ) { Text("Login") }
            }

            // Bottom Navigation
            BottomNavigationBar(
                    selectedItem = BottomNavItem.Login,
                    onItemSelected = { item ->
                        when (item) {
                            BottomNavItem.Login -> {
                                // Already on login
                            }
                            BottomNavItem.Home -> {
                                // Navigate to dashboard (home) - but only if logged in
                                if (username.isNotEmpty() && password.isNotEmpty()) {
                                    onNavigateToDashboard(username, password)
                                }
                            }
                            BottomNavItem.Profile -> {
                                // Can't access profile without login
                            }
                        }
                    }
            )
        }
    }
}

@Preview
@Composable
fun LoginScreenPreview() {
    ApplicationTheme { LoginScreen(onNavigateToDashboard = { _, _ -> }) }
}
