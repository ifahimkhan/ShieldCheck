package com.fahim.shieldcheck.domain.usecase.app

import com.fahim.shieldcheck.domain.model.app.RiskLevel
import com.fahim.shieldcheck.domain.repository.AppRepository
import javax.inject.Inject

data class AppStatistics(
    val totalApps: Int,
    val criticalCount: Int,
    val highRiskCount: Int,
    val mediumRiskCount: Int,
    val lowRiskCount: Int,
    val safeCount: Int,
    val averageRiskScore: Double
)

class GetAppStatisticsUseCase @Inject constructor(
    private val appRepository: AppRepository
) {
    suspend operator fun invoke(): AppStatistics {
        val criticalCount = appRepository.getAppCountByRiskLevel(RiskLevel.CRITICAL)
        val highCount = appRepository.getAppCountByRiskLevel(RiskLevel.HIGH)
        val mediumCount = appRepository.getAppCountByRiskLevel(RiskLevel.MEDIUM)
        val lowCount = appRepository.getAppCountByRiskLevel(RiskLevel.LOW)
        val safeCount = appRepository.getAppCountByRiskLevel(RiskLevel.SAFE)
        val averageScore = appRepository.getAverageRiskScore()

        return AppStatistics(
            totalApps = criticalCount + highCount + mediumCount + lowCount + safeCount,
            criticalCount = criticalCount,
            highRiskCount = highCount,
            mediumRiskCount = mediumCount,
            lowRiskCount = lowCount,
            safeCount = safeCount,
            averageRiskScore = averageScore
        )
    }
}
