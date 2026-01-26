package com.fahim.shieldcheck.core.utils

import com.fahim.shieldcheck.core.constants.PermissionConstants
import com.fahim.shieldcheck.domain.model.app.AppPermission
import com.fahim.shieldcheck.domain.model.app.RiskLevel
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class PermissionAnalyzer @Inject constructor() {

    fun analyzePermission(permission: String, isGranted: Boolean = true): AppPermission {
        val riskScore = PermissionConstants.getPermissionRiskScore(permission)
        val description = PermissionConstants.getPermissionDescription(permission)
        val isDangerous = PermissionConstants.isDangerousPermission(permission)

        return AppPermission(
            name = permission,
            description = description,
            riskScore = riskScore,
            riskLevel = RiskLevel.fromScore(riskScore),
            isGranted = isGranted,
            isDangerous = isDangerous
        )
    }

    fun analyzePermissions(permissions: List<String>): List<AppPermission> {
        return permissions.map { analyzePermission(it) }
            .sortedByDescending { it.riskScore }
    }

    fun categorizePermissions(permissions: List<AppPermission>): Map<RiskLevel, List<AppPermission>> {
        return permissions.groupBy { it.riskLevel }
    }

    fun getDangerousPermissions(permissions: List<AppPermission>): List<AppPermission> {
        return permissions.filter { it.isDangerous }
    }

    fun getPermissionsByCategory(permissions: List<String>): PermissionCategories {
        val critical = permissions.filter { PermissionConstants.CRITICAL_PERMISSIONS.contains(it) }
        val high = permissions.filter { PermissionConstants.HIGH_RISK_PERMISSIONS.contains(it) }
        val medium = permissions.filter { PermissionConstants.MEDIUM_RISK_PERMISSIONS.contains(it) }
        val low = permissions.filter { PermissionConstants.LOW_RISK_PERMISSIONS.contains(it) }
        val other = permissions.filter {
            !PermissionConstants.CRITICAL_PERMISSIONS.contains(it) &&
            !PermissionConstants.HIGH_RISK_PERMISSIONS.contains(it) &&
            !PermissionConstants.MEDIUM_RISK_PERMISSIONS.contains(it) &&
            !PermissionConstants.LOW_RISK_PERMISSIONS.contains(it)
        }

        return PermissionCategories(
            critical = critical,
            high = high,
            medium = medium,
            low = low,
            other = other
        )
    }
}

data class PermissionCategories(
    val critical: List<String>,
    val high: List<String>,
    val medium: List<String>,
    val low: List<String>,
    val other: List<String>
)
