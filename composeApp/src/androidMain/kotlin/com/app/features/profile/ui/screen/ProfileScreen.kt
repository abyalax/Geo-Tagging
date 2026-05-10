package com.app.features.profile.ui.screen

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.app.core.theme.ApplicationTheme
import com.app.ui.components.BottomNavItem
import com.app.ui.components.BottomNavigationBar
import com.app.ui.components.TopAppBar

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ProfileScreen(
        username: String = "User",
        password: String = "",
        onUsernameChange: (String) -> Unit = {},
        onPasswordChange: (String) -> Unit = {},
        onSaveProfile: () -> Unit = {},
        onNavigateToHome: () -> Unit = {},
        onNavigateToLogin: () -> Unit = {},
        onLogout: () -> Unit = {}
) {
        Surface(modifier = Modifier.fillMaxSize(), color = MaterialTheme.colorScheme.background) {
                Column(modifier = Modifier.fillMaxSize()) {
                        // Top App Bar
                        TopAppBar(
                                username = username,
                                onProfileClick = { /* Handle profile click */},
                                onNotificationClick = { /* Handle notification click */},
                                onStatsClick = { /* Handle stats click */},
                                onLogout = onLogout
                        )

                        // Profile Content
                        Column(
                                modifier = Modifier.weight(1f).fillMaxWidth().padding(24.dp),
                                horizontalAlignment = Alignment.CenterHorizontally,
                                verticalArrangement = Arrangement.spacedBy(24.dp)
                        ) {
                                // Avatar
                                Box(
                                        modifier =
                                                Modifier.size(120.dp)
                                                        .background(
                                                                color =
                                                                        MaterialTheme.colorScheme
                                                                                .primary,
                                                                shape = CircleShape
                                                        ),
                                        contentAlignment = Alignment.Center
                                ) {
                                        Text(
                                                text = username.take(2).uppercase(),
                                                style = MaterialTheme.typography.headlineLarge,
                                                color = MaterialTheme.colorScheme.onPrimary,
                                                fontWeight = FontWeight.Bold,
                                                fontSize = 36.sp
                                        )
                                }

                                // Username Field
                                OutlinedTextField(
                                        value = username,
                                        onValueChange = onUsernameChange,
                                        label = { Text("Username") },
                                        modifier = Modifier.fillMaxWidth(),
                                        singleLine = true
                                )

                                // Password Field
                                OutlinedTextField(
                                        value = password,
                                        onValueChange = onPasswordChange,
                                        label = { Text("Password") },
                                        visualTransformation = PasswordVisualTransformation(),
                                        modifier = Modifier.fillMaxWidth(),
                                        singleLine = true
                                )

                                // Save Button
                                Button(
                                        onClick = onSaveProfile,
                                        modifier = Modifier.fillMaxWidth().height(50.dp),
                                        colors =
                                                ButtonDefaults.buttonColors(
                                                        containerColor =
                                                                MaterialTheme.colorScheme.primary
                                                )
                                ) {
                                        Text(
                                                text = "Save Profile",
                                                style = MaterialTheme.typography.titleMedium,
                                                color = MaterialTheme.colorScheme.onPrimary
                                        )
                                }
                        }

                        // Bottom Navigation
                        BottomNavigationBar(
                                selectedItem = BottomNavItem.Profile,
                                onItemSelected = { item ->
                                        when (item) {
                                                BottomNavItem.Login -> onNavigateToLogin()
                                                BottomNavItem.Home -> onNavigateToHome()
                                                BottomNavItem.Profile -> {} // already on profile
                                        }
                                }
                        )
                }
        }
}

@Preview(showBackground = true)
@Composable
fun ProfileScreenPreview() {
        ApplicationTheme { ProfileScreen(username = "John Doe", password = "password123") }
}
