package com.app.features.auth.login.ui.components

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.app.core.theme.ApplicationTheme

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun LoginForm(
        username: String,
        onUsernameChange: (String) -> Unit,
        password: String,
        onPasswordChange: (String) -> Unit,
        modifier: Modifier = Modifier
) {
  Column(modifier = modifier.fillMaxWidth().padding(top = 16.dp)) {
    OutlinedTextField(
            label = { Text("Username") },
            value = username,
            onValueChange = onUsernameChange,
            modifier = Modifier.fillMaxWidth().padding(bottom = 12.dp),
            placeholder = { Text("Enter your username") },
            singleLine = true
    )

    OutlinedTextField(
            label = { Text("Password") },
            value = password,
            onValueChange = onPasswordChange,
            modifier = Modifier.fillMaxWidth(),
            placeholder = { Text("Enter your password") },
            visualTransformation = PasswordVisualTransformation(),
            singleLine = true
    )
  }
}

@Preview(showBackground = true)
@Composable
fun LoginFormPreview() {
  ApplicationTheme {
    LoginForm(
            username = "admin",
            onUsernameChange = {},
            password = "password123",
            onPasswordChange = {}
    )
  }
}
