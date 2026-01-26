package com.fahim.shieldcheck.core.utils

import com.fahim.shieldcheck.core.constants.PermissionConstants
import com.fahim.shieldcheck.domain.model.app.RiskLevel
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class RiskCalculator @Inject constructor() {

    fun calculateAppRiskScore(
        permissions: List<String>,
        isSystemApp: Boolean = false
    ): Int {
        var score = 0

        permissions.forEach { permission ->
            score += PermissionConstants.getPermissionRiskScore(permission)
        }

        // System apps get a slight reduction as they're more trusted
        if (isSystemApp) {
            score = (score * 0.7).toInt()
        }

        // Cap the score at 100
        return score.coerceIn(0, 100)
    }

    fun calculateRiskLevel(score: Int): RiskLevel {
        return RiskLevel.fromScore(score)
    }

    fun calculateOverallSecurityScore(
        appRiskScore: Double,
        deviceSecurityScore: Int,
        networkSecurityScore: Int
    ): Int {
        // Weighted average: Device 40%, Apps 35%, Network 25%
        val weightedScore = (deviceSecurityScore * 0.40) +
                ((100 - appRiskScore) * 0.35) +
                (networkSecurityScore * 0.25)

        return weightedScore.toInt().coerceIn(0, 100)
    }

    fun getSecurityGrade(score: Int): String {
        return when {
            score >= 90 -> "A"
            score >= 75 -> "B"
            score >= 60 -> "C"
            score >= 40 -> "D"
            else -> "F"
        }
    }

    fun getRiskSummary(
        criticalCount: Int,
        highCount: Int,
        mediumCount: Int
    ): String {
        return buildString {
            if (criticalCount > 0) append("$criticalCount critical, ")
            if (highCount > 0) append("$highCount high risk, ")
            if (mediumCount > 0) append("$mediumCount medium risk")
            if (isEmpty()) append("No significant risks found")
        }.trimEnd(',', ' ')
    }
}
