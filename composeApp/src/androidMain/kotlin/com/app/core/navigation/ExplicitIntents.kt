package com.app.core.navigation

import android.content.Context
import android.content.Intent
import com.app.core.common.Constants
import com.app.features.dashboard.activities.DashboardActivity

object ExplicitIntents {

    fun navigateToDashboard(context: Context, username: String, password: String) {
        // Save login state to SharedPreferences
        val prefs = context.getSharedPreferences("app_prefs", Context.MODE_PRIVATE)
        prefs.edit()
                .putString("username", username)
                .putString("password", password)
                .putBoolean("is_logged_in", true)
                .apply()

        val intent =
                Intent(context, DashboardActivity::class.java).apply {
                    putExtra(Constants.EXTRA_USERNAME, username)
                }
        context.startActivity(intent)
    }

    fun logout(context: Context) {
        // Clear login state from SharedPreferences
        val prefs = context.getSharedPreferences("app_prefs", Context.MODE_PRIVATE)
        prefs.edit().clear().apply()
    }
}
