package com.app.features.auth.login.ui.screen

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

@Composable
fun LoginScreen(onNavigate: (String, String) -> Unit) {
    var username by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }

    Surface(modifier = Modifier.fillMaxSize(), color = MaterialTheme.colorScheme.background) {
        Column(
                modifier = Modifier.fillMaxSize().padding(16.dp),
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
                        // Simple validation
                        if (username.isNotEmpty() && password.isNotEmpty()) {
                            onNavigate(username, password)
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
    }
}

@Preview
@Composable
fun LoginScreenPreview() {
    ApplicationTheme { LoginScreen(onNavigate = { _, _ -> }) }
}
