package com.app.core.data.repository

import android.content.Context
import com.app.core.domain.model.UserSession
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flowOf

class AuthRepositoryImpl(private val context: Context) : AuthRepository {
    override suspend fun login(username: String, password: String): Result<UserSession> {
        return try {
            val prefs = context.getSharedPreferences("app_prefs", Context.MODE_PRIVATE)
            prefs.edit().apply {
                putString("username", username)
                putString("password", password)
                putBoolean("is_logged_in", true)
            }.commit()
            Result.success(UserSession(isLoggedIn = true, username = username))
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override suspend fun logout(): Result<Unit> {
        return try {
            val prefs = context.getSharedPreferences("app_prefs", Context.MODE_PRIVATE)
            prefs.edit().clear().commit()
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override fun getCurrentSession(): Flow<UserSession> {
        val prefs = context.getSharedPreferences("app_prefs", Context.MODE_PRIVATE)
        val isLoggedIn = prefs.getBoolean("is_logged_in", false)
        val username = prefs.getString("username", null)
        return flowOf(UserSession(isLoggedIn = isLoggedIn, username = username))
    }

    override suspend fun updateProfile(username: String, password: String): Result<Unit> {
        return try {
            val prefs = context.getSharedPreferences("app_prefs", Context.MODE_PRIVATE)
            prefs.edit().apply {
                putString("username", username)
                putString("password", password)
            }.commit()
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}
