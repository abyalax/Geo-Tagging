package com.app.core.domain.model

data class UserSession(
    val isLoggedIn: Boolean = false,
    val username: String? = null,
    val password: String? = null
)
