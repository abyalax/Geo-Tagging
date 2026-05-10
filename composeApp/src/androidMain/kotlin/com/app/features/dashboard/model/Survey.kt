package com.app.features.dashboard.model

enum class SurveyStatus {
    OPEN,
    VERIFIED,
    REJECTED
}

data class Survey(
    val id: Int,
    val title: String,
    val description: String,
    val latitude: Double,
    val longitude: Double,
    val status: SurveyStatus = SurveyStatus.OPEN,
    val createdAt: Long = System.currentTimeMillis(),
    val verifiedBy: String? = null
)