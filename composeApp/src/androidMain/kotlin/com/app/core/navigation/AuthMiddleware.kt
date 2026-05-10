package com.app.core.navigation

import android.content.Context
import android.content.Intent
import com.app.features.auth.login.activities.LoginActivity

/**
 * Authentication Middleware for protecting routes
 * Ensures user is logged in before accessing protected screens
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
     * Redirect to login if not authenticated
     * @param context Activity context
     * @return true if user is authenticated, false if redirected to login
     */
    fun requireAuth(context: Context): Boolean {
        return if (isLoggedIn(context)) {
            true
        } else {
            // Redirect to login
            val loginIntent = Intent(context, LoginActivity::class.java).apply {
                flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
            }
            context.startActivity(loginIntent)
            false
        }
    }
    
    /**
     * Logout user and clear session
     * @param context Application context
     */
    fun logout(context: Context) {
        val prefs = context.getSharedPreferences("app_prefs", Context.MODE_PRIVATE)
        prefs.edit().clear().apply()
        
        // Redirect to login
        val loginIntent = Intent(context, LoginActivity::class.java).apply {
            flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        }
        context.startActivity(loginIntent)
    }
}
