package com.app

import android.content.Intent
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.enableEdgeToEdge
import androidx.compose.runtime.Composable
import androidx.compose.ui.tooling.preview.Preview
import com.app.core.navigation.AuthMiddleware

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        enableEdgeToEdge()
        super.onCreate(savedInstanceState)

        // Check login state using AuthMiddleware and navigate accordingly
        if (AuthMiddleware.isLoggedIn(this)) {
            // User is logged in, go to Dashboard
            startActivity(
                    Intent(
                            this,
                            com.app.features.dashboard.activities.DashboardActivity::class.java
                    )
            )
        } else {
            // User not logged in, go to Login
            startActivity(
                    Intent(this, com.app.features.auth.login.activities.LoginActivity::class.java)
            )
        }

        // Close MainActivity since we're navigating away
        finish()
    }
}

@Preview
@Composable
fun AppAndroidPreview() {
    App()
}
