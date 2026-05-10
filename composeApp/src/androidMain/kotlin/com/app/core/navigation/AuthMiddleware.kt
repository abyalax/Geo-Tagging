package com.app.core.navigation

import android.content.Context
import android.util.Log
import androidx.navigation.NavController
import com.app.navigation.Routes

/**
 * Authentication Middleware for protecting routes Ensures user is logged in before accessing
 * protected screens
 */
object AuthMiddleware {

    /**
     * Check if user is logged in
     * @param context Application context
     * @return true if user is logged in, false otherwise
     */
    fun isLoggedIn(context: Context): Boolean {
        val prefs = context.getSharedPreferences("app_prefs", Context.MODE_PRIVATE)
        return prefs.getBoolean("is_logged_in", false)
    }

    /**
     * Login user and save authentication state
     * @param context Application context
     * @param username Username to save
     * @param password Password to save (NOTE: In production, use secure token-based auth instead)
     */
    fun login(context: Context, username: String, password: String) {
        Log.d("AuthMiddleware", "login() called with username: '$username'")
        val prefs = context.getSharedPreferences("app_prefs", Context.MODE_PRIVATE)
        prefs.edit().apply {
            putBoolean("is_logged_in", true)
            putString("username", username)
            putString("password", password)
            apply()
        }
        Log.d("AuthMiddleware", "Login state saved successfully")
        Log.d("AuthMiddleware", "isLoggedIn check: ${isLoggedIn(context)}")
    }

    /**
     * Get logged in username
     * @param context Application context
     * @return username if logged in, null otherwise
     */
    fun getUsername(context: Context): String? {
        val prefs = context.getSharedPreferences("app_prefs", Context.MODE_PRIVATE)
        return if (isLoggedIn(context)) {
            prefs.getString("username", null)
        } else {
            null
        }
    }

    /**
     * Logout user and clear session
     * @param context Application context
     * @param navController Navigation controller to navigate to login
     */
    fun logout(context: Context, navController: NavController) {
        Log.d("AuthMiddleware", "logout() called")
        val prefs = context.getSharedPreferences("app_prefs", Context.MODE_PRIVATE)

        // Clear all session data
        prefs.edit().clear().apply()
        Log.d("AuthMiddleware", "Session data cleared")

        // ✅ FIXED: Use Routes sealed class instead of hardcoded string
        navController.navigate(Routes.Login.route) {
            popUpTo(Routes.Login.route) { inclusive = true }
        }
        Log.d("AuthMiddleware", "Navigated to login route")
    }
}
