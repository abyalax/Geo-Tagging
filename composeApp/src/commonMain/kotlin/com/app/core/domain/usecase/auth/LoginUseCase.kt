package com.app.core.domain.usecase.auth

import com.app.core.data.repository.AuthRepository
import com.app.core.domain.model.UserSession

class LoginUseCase(
    private val authRepository: AuthRepository
) {
    suspend operator fun invoke(username: String, password: String): Result<UserSession> {
        return authRepository.login(username, password)
    }
}
