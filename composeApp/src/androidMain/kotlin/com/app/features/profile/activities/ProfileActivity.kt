package com.app.features.profile.activities

import android.content.Intent
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import com.app.core.navigation.AuthMiddleware
import com.app.core.theme.ApplicationTheme
import com.app.features.dashboard.activities.DashboardActivity
import com.app.features.profile.ui.screen.ProfileScreen

class ProfileActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        enableEdgeToEdge()
        super.onCreate(savedInstanceState)

        // Check authentication - redirect to login if not authenticated
        if (!AuthMiddleware.requireAuth(this)) {
            return // Activity will be finished by middleware
        }

        // Get user data from middleware (must be authenticated)
        val prefs = getSharedPreferences("app_prefs", MODE_PRIVATE)
        val savedUsername =
                prefs.getString("username", null)
                        ?: run {
                            AuthMiddleware.logout(this)
                            return
                        }
        val savedPassword = prefs.getString("password", "") ?: ""

        setContent {
            ApplicationTheme {
                var username by remember { mutableStateOf(savedUsername) }
                var password by remember { mutableStateOf(savedPassword) }

                ProfileScreen(
                        username = username,
                        password = password,
                        onUsernameChange = { username = it },
                        onPasswordChange = { password = it },
                        onSaveProfile = {
                            // Save updated profile to SharedPreferences
                            prefs.edit()
                                    .putString("username", username)
                                    .putString("password", password)
                                    .apply()
                        },
                        onNavigateToHome = {
                            // Navigate back to Dashboard (Home)
                            startActivity(Intent(this, DashboardActivity::class.java))
                            finish() // Close ProfileActivity to avoid back stack issues
                        },
                        onNavigateToLogin = { AuthMiddleware.logout(this) },
                        onLogout = { AuthMiddleware.logout(this) }
                )
            }
        }
    }
}
