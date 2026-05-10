package com.app.features.auth.login.activities

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import com.app.core.navigation.ExplicitIntents
import com.app.core.theme.ApplicationTheme
import com.app.features.auth.login.ui.screen.LoginScreen

class LoginActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        enableEdgeToEdge()
        super.onCreate(savedInstanceState)
        setContent {
            ApplicationTheme {
                LoginScreen(
                        onNavigate = { username, password ->
                            ExplicitIntents.navigateToDashboard(
                                    context = this@LoginActivity,
                                    username = username,
                                    password = password
                            )
                        }
                )
            }
        }
    }
}
