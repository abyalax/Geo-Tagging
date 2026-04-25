package com.app.features.auth.login.activities

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import com.app.core.navigation.IntentManager
import com.app.core.theme.ApplicationTheme
import com.app.features.auth.login.ui.screen.LoginScreen

class LoginActivity : ComponentActivity() {
  override fun onCreate(savedInstanceState: Bundle?) {
    enableEdgeToEdge()
    super.onCreate(savedInstanceState)
    setContent {
        ApplicationTheme {
            LoginScreen(
                onNavigate = { username ->
                    // For now we use default coordinates as we removed them from login
                    IntentManager.navigateToDashboard(
                        context = this@LoginActivity,
                        sensorName = username,
                        latitude = "-6.2088", 
                        longitude = "106.8456"
                    )
                }
            )
        }
    }
  }
}
