package com.app.core.domain.usecase.auth

import com.app.core.data.repository.AuthRepository
import com.app.core.domain.model.UserSession
import kotlinx.coroutines.flow.Flow

class GetSessionUseCase(
    private val authRepository: AuthRepository
) {
    operator fun invoke(): Flow<UserSession> {
        return authRepository.getCurrentSession()
    }
}
