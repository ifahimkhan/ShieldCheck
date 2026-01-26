package com.fahim.shieldcheck.domain.model.dashboard

import java.util.Date

data class PrivacySummary(
    val overallScore: Int,
    val grade: String,
    val appRiskScore: Double,
    val deviceSecurityScore: Int,
    val networkSecurityScore: Int,
    val totalAppsScanned: Int,
    val criticalAppsCount: Int,
    val highRiskAppsCount: Int,
    val deviceIssuesCount: Int,
    val networkIssuesCount: Int,
    val lastScanDate: Date?,
    val recommendations: List<SecurityRecommendation>
) {
    val hasIssues: Boolean
        get() = criticalAppsCount > 0 || deviceIssuesCount > 0 || networkIssuesCount > 0

    val totalIssues: Int
        get() = criticalAppsCount + highRiskAppsCount + deviceIssuesCount + networkIssuesCount
}

data class SecurityRecommendation(
    val title: String,
    val description: String,
    val priority: RecommendationPriority,
    val actionType: RecommendationAction
)

enum class RecommendationPriority {
    HIGH,
    MEDIUM,
    LOW
}

enum class RecommendationAction {
    REVIEW_APPS,
    DEVICE_SETTINGS,
    NETWORK_SETTINGS,
    GENERAL
}

data class SecurityScore(
    val score: Int,
    val maxScore: Int = 100,
    val breakdown: ScoreBreakdown
) {
    val percentage: Float
        get() = score.toFloat() / maxScore

    val grade: String
        get() = when {
            score >= 90 -> "A"
            score >= 75 -> "B"
            score >= 60 -> "C"
            score >= 40 -> "D"
            else -> "F"
        }
}

data class ScoreBreakdown(
    val appScore: Int,
    val deviceScore: Int,
    val networkScore: Int,
    val appWeight: Float = 0.35f,
    val deviceWeight: Float = 0.40f,
    val networkWeight: Float = 0.25f
)
