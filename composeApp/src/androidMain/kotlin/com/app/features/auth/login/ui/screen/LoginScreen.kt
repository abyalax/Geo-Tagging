package com.app.features.auth.login.ui.screen

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.app.core.theme.ApplicationTheme
import com.app.features.auth.login.ui.components.LoginForm

@Composable
fun LoginScreen(onNavigate: (String) -> Unit) {
  var username by remember { mutableStateOf("") }
  var password by remember { mutableStateOf("") }

  Surface(
          modifier = Modifier.fillMaxSize(),
          color = MaterialTheme.colorScheme.background
  ) {
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
                    onNavigate(username) 
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
  ApplicationTheme { LoginScreen(onNavigate = { _ -> }) }
}
