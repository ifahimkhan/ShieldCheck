package com.fahim.shieldcheck.data.repository

import com.fahim.shieldcheck.core.utils.RiskCalculator
import com.fahim.shieldcheck.data.local.db.dao.AppScanResultDao
import com.fahim.shieldcheck.data.local.db.dao.DeviceScanResultDao
import com.fahim.shieldcheck.data.local.db.dao.NetworkScanResultDao
import com.fahim.shieldcheck.domain.model.app.RiskLevel
import com.fahim.shieldcheck.domain.model.dashboard.PrivacySummary
import com.fahim.shieldcheck.domain.model.dashboard.RecommendationAction
import com.fahim.shieldcheck.domain.model.dashboard.RecommendationPriority
import com.fahim.shieldcheck.domain.model.dashboard.ScoreBreakdown
import com.fahim.shieldcheck.domain.model.dashboard.SecurityRecommendation
import com.fahim.shieldcheck.domain.model.dashboard.SecurityScore
import com.fahim.shieldcheck.domain.repository.PrivacyDashboardRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.withContext
import java.util.Date
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class PrivacyDashboardRepositoryImpl @Inject constructor(
    private val appScanResultDao: AppScanResultDao,
    private val deviceScanResultDao: DeviceScanResultDao,
    private val networkScanResultDao: NetworkScanResultDao,
    private val riskCalculator: RiskCalculator
) : PrivacyDashboardRepository {

    override suspend fun getPrivacySummary(): PrivacySummary {
        return withContext(Dispatchers.IO) {
            val appRiskScore = appScanResultDao.getAverageRiskScore() ?: 0.0
            val criticalCount = appScanResultDao.getCountByRiskLevel(RiskLevel.CRITICAL.name)
            val highCount = appScanResultDao.getCountByRiskLevel(RiskLevel.HIGH.name)
            val totalApps = appScanResultDao.getAppCount()

            var deviceScore = 100
            var deviceIssues = 0
            var lastScanDate: Date? = null

            deviceScanResultDao.getLatestDeviceScanResult().firstOrNull()?.let { device ->
                deviceScore = device.overallScore
                lastScanDate = device.scanDate
                if (device.isRooted) deviceIssues++
                if (!device.isEncrypted) deviceIssues++
                if (!device.hasScreenLock) deviceIssues++
                if (device.isDeveloperOptionsEnabled) deviceIssues++
                if (device.isUsbDebuggingEnabled) deviceIssues++
            }

            var networkScore = 100
            var networkIssues = 0

            networkScanResultDao.getLatestNetworkScanResult().firstOrNull()?.let { network ->
                networkScore = network.overallScore
                if (!network.isWifiSecure) networkIssues++
                if (network.openPorts.isNotEmpty()) networkIssues++
            }

            val overallScore = riskCalculator.calculateOverallSecurityScore(
                appRiskScore = appRiskScore,
                deviceSecurityScore = deviceScore,
                networkSecurityScore = networkScore
            )

            val recommendations = generateRecommendations(
                criticalCount = criticalCount,
                highCount = highCount,
                deviceIssues = deviceIssues,
                networkIssues = networkIssues,
                deviceScore = deviceScore,
                networkScore = networkScore
            )

            PrivacySummary(
                overallScore = overallScore,
                grade = riskCalculator.getSecurityGrade(overallScore),
                appRiskScore = appRiskScore,
                deviceSecurityScore = deviceScore,
                networkSecurityScore = networkScore,
                totalAppsScanned = totalApps,
                criticalAppsCount = criticalCount,
                highRiskAppsCount = highCount,
                deviceIssuesCount = deviceIssues,
                networkIssuesCount = networkIssues,
                lastScanDate = lastScanDate,
                recommendations = recommendations
            )
        }
    }

    override fun observePrivacySummary(): Flow<PrivacySummary> = flow {
        emit(getPrivacySummary())
    }

    override suspend fun calculateOverallSecurityScore(): SecurityScore {
        return withContext(Dispatchers.IO) {
            val appRiskScore = appScanResultDao.getAverageRiskScore() ?: 0.0
            val appScore = (100 - appRiskScore).toInt().coerceIn(0, 100)

            var deviceScore = 100
            deviceScanResultDao.getLatestDeviceScanResult().firstOrNull()?.let {
                deviceScore = it.overallScore
            }

            var networkScore = 100
            networkScanResultDao.getLatestNetworkScanResult().firstOrNull()?.let {
                networkScore = it.overallScore
            }

            val breakdown = ScoreBreakdown(
                appScore = appScore,
                deviceScore = deviceScore,
                networkScore = networkScore
            )

            val overallScore = (
                appScore * breakdown.appWeight +
                deviceScore * breakdown.deviceWeight +
                networkScore * breakdown.networkWeight
            ).toInt()

            SecurityScore(
                score = overallScore,
                breakdown = breakdown
            )
        }
    }

    override suspend fun refreshAllScans() {
        // This would trigger all scans - implementation depends on scan managers
    }

    private fun generateRecommendations(
        criticalCount: Int,
        highCount: Int,
        deviceIssues: Int,
        networkIssues: Int,
        deviceScore: Int,
        networkScore: Int
    ): List<SecurityRecommendation> {
        val recommendations = mutableListOf<SecurityRecommendation>()

        if (criticalCount > 0) {
            recommendations.add(
                SecurityRecommendation(
                    title = "Review Critical Risk Apps",
                    description = "You have $criticalCount apps with critical risk permissions. Consider reviewing or uninstalling them.",
                    priority = RecommendationPriority.HIGH,
                    actionType = RecommendationAction.REVIEW_APPS
                )
            )
        }

        if (highCount > 0) {
            recommendations.add(
                SecurityRecommendation(
                    title = "Review High Risk Apps",
                    description = "You have $highCount apps with high risk permissions.",
                    priority = RecommendationPriority.MEDIUM,
                    actionType = RecommendationAction.REVIEW_APPS
                )
            )
        }

        if (deviceIssues > 0) {
            recommendations.add(
                SecurityRecommendation(
                    title = "Address Device Security Issues",
                    description = "Found $deviceIssues device security concerns that need attention.",
                    priority = RecommendationPriority.HIGH,
                    actionType = RecommendationAction.DEVICE_SETTINGS
                )
            )
        }

        if (networkIssues > 0) {
            recommendations.add(
                SecurityRecommendation(
                    title = "Improve Network Security",
                    description = "Your network has $networkIssues security issues.",
                    priority = RecommendationPriority.MEDIUM,
                    actionType = RecommendationAction.NETWORK_SETTINGS
                )
            )
        }

        if (recommendations.isEmpty()) {
            recommendations.add(
                SecurityRecommendation(
                    title = "Keep Up the Good Work!",
                    description = "Your device security looks good. Keep scanning regularly.",
                    priority = RecommendationPriority.LOW,
                    actionType = RecommendationAction.GENERAL
                )
            )
        }

        return recommendations.sortedBy { it.priority.ordinal }
    }
}
