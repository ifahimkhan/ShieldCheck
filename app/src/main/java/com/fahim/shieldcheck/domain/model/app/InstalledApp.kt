package com.fahim.shieldcheck.domain.model.app

import android.graphics.drawable.Drawable
import java.util.Date

data class InstalledApp(
    val packageName: String,
    val appName: String,
    val versionName: String?,
    val versionCode: Long,
    val isSystemApp: Boolean,
    val installedDate: Date?,
    val lastUpdatedDate: Date?,
    val icon: Drawable?,
    val permissions: List<AppPermission>,
    val riskScore: Int,
    val riskLevel: RiskLevel
) {
    val dangerousPermissions: List<AppPermission>
        get() = permissions.filter { it.isDangerous }

    val dangerousPermissionCount: Int
        get() = dangerousPermissions.size

    val totalPermissionCount: Int
        get() = permissions.size
}
