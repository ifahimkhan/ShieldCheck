package com.fahim.shieldcheck.data.mapper

import android.content.pm.PackageInfo
import android.graphics.drawable.Drawable
import com.fahim.shieldcheck.core.utils.PermissionAnalyzer
import com.fahim.shieldcheck.core.utils.RiskCalculator
import com.fahim.shieldcheck.data.local.datasource.AppDataSource
import com.fahim.shieldcheck.data.local.db.entity.AppScanResultEntity
import com.fahim.shieldcheck.domain.model.app.AppPermission
import com.fahim.shieldcheck.domain.model.app.InstalledApp
import com.fahim.shieldcheck.domain.model.app.RiskLevel
import java.util.Date
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class AppMapper @Inject constructor(
    private val appDataSource: AppDataSource,
    private val permissionAnalyzer: PermissionAnalyzer,
    private val riskCalculator: RiskCalculator
) {

    fun mapPackageInfoToInstalledApp(packageInfo: PackageInfo): InstalledApp {
        val permissions = appDataSource.getRequestedPermissions(packageInfo)
        val analyzedPermissions = permissionAnalyzer.analyzePermissions(permissions)
        val isSystemApp = appDataSource.isSystemApp(packageInfo)
        val riskScore = riskCalculator.calculateAppRiskScore(permissions, isSystemApp)

        return InstalledApp(
            packageName = packageInfo.packageName,
            appName = appDataSource.getAppName(packageInfo),
            versionName = appDataSource.getVersionName(packageInfo),
            versionCode = appDataSource.getVersionCode(packageInfo),
            isSystemApp = isSystemApp,
            installedDate = appDataSource.getInstalledDate(packageInfo),
            lastUpdatedDate = appDataSource.getLastUpdatedDate(packageInfo),
            icon = appDataSource.getAppIcon(packageInfo),
            permissions = analyzedPermissions,
            riskScore = riskScore,
            riskLevel = RiskLevel.fromScore(riskScore)
        )
    }

    fun mapInstalledAppToEntity(app: InstalledApp): AppScanResultEntity {
        return AppScanResultEntity(
            packageName = app.packageName,
            appName = app.appName,
            versionName = app.versionName,
            versionCode = app.versionCode,
            isSystemApp = app.isSystemApp,
            installedDate = app.installedDate,
            lastUpdatedDate = app.lastUpdatedDate,
            permissions = app.permissions.map { it.name },
            dangerousPermissions = app.dangerousPermissions.map { it.name },
            riskScore = app.riskScore,
            riskLevel = app.riskLevel.name,
            lastScanDate = Date()
        )
    }

    fun mapEntityToInstalledApp(
        entity: AppScanResultEntity,
        icon: Drawable? = null
    ): InstalledApp {
        val analyzedPermissions = permissionAnalyzer.analyzePermissions(entity.permissions)

        return InstalledApp(
            packageName = entity.packageName,
            appName = entity.appName,
            versionName = entity.versionName,
            versionCode = entity.versionCode,
            isSystemApp = entity.isSystemApp,
            installedDate = entity.installedDate,
            lastUpdatedDate = entity.lastUpdatedDate,
            icon = icon,
            permissions = analyzedPermissions,
            riskScore = entity.riskScore,
            riskLevel = RiskLevel.fromString(entity.riskLevel)
        )
    }
}
