package com.app.features.dashboard.domain.model

enum class SurveyStatus {
    OPEN,
    VERIFIED,
    REJECTED
}

data class Survey(
    val id: Int,
    val title: String,
    val description: String,
    val status: SurveyStatus,
    val location: String,
    val createdDate: String
)

data class SurveyStats(
    val total: Int,
    val open: Int,
    val verified: Int,
    val rejected: Int
)
