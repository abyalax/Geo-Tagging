package com.app.core.data.repository

import com.app.core.domain.model.UserSession
import kotlinx.coroutines.flow.Flow

interface AuthRepository {
    suspend fun login(username: String, password: String): Result<UserSession>
    suspend fun logout(): Result<Unit>
    fun getCurrentSession(): Flow<UserSession>
    suspend fun updateProfile(username: String, password: String): Result<Unit>
}
