package com.fahim.shieldcheck.data.repository

import com.fahim.shieldcheck.data.local.datasource.DeviceDataSource
import com.fahim.shieldcheck.data.local.db.dao.DeviceScanResultDao
import com.fahim.shieldcheck.data.local.db.entity.DeviceScanResultEntity
import com.fahim.shieldcheck.domain.model.device.DeviceSecurityStatus
import com.fahim.shieldcheck.domain.model.device.IssueSeverity
import com.fahim.shieldcheck.domain.model.device.ScreenLockType
import com.fahim.shieldcheck.domain.model.device.SecurityIssue
import com.fahim.shieldcheck.domain.repository.DeviceSecurityRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.withContext
import java.util.Date
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class DeviceSecurityRepositoryImpl @Inject constructor(
    private val deviceDataSource: DeviceDataSource,
    private val deviceScanResultDao: DeviceScanResultDao
) : DeviceSecurityRepository {

    override suspend fun checkDeviceSecurity(): DeviceSecurityStatus {
        return withContext(Dispatchers.IO) {
            deviceDataSource.getDeviceSecurityStatus()
        }
    }

    override fun getLatestDeviceScanResult(): Flow<DeviceSecurityStatus?> {
        return deviceScanResultDao.getLatestDeviceScanResult().map { entity ->
            entity?.let { mapEntityToStatus(it) }
        }
    }

    override suspend fun saveDeviceScanResult(status: DeviceSecurityStatus) {
        withContext(Dispatchers.IO) {
            val entity = DeviceScanResultEntity(
                isRooted = status.isRooted,
                isEncrypted = status.isEncrypted,
                hasScreenLock = status.hasScreenLock,
                isDeveloperOptionsEnabled = status.isDeveloperOptionsEnabled,
                isUsbDebuggingEnabled = status.isUsbDebuggingEnabled,
                isUnknownSourcesEnabled = status.isUnknownSourcesEnabled,
                securityPatchLevel = status.securityPatchLevel,
                androidVersion = status.androidVersion,
                deviceModel = status.deviceModel,
                overallScore = status.overallScore,
                scanDate = Date()
            )
            deviceScanResultDao.insertDeviceScanResult(entity)
            deviceScanResultDao.deleteOldResults(10)
        }
    }

    override suspend fun isDeviceRooted(): Boolean {
        return withContext(Dispatchers.IO) {
            deviceDataSource.isRooted()
        }
    }

    override suspend fun isDeviceEncrypted(): Boolean {
        return withContext(Dispatchers.IO) {
            deviceDataSource.isEncrypted()
        }
    }

    override suspend fun hasScreenLock(): Boolean {
        return withContext(Dispatchers.IO) {
            deviceDataSource.hasScreenLock()
        }
    }

    override suspend fun isDeveloperOptionsEnabled(): Boolean {
        return withContext(Dispatchers.IO) {
            deviceDataSource.isDeveloperOptionsEnabled()
        }
    }

    override suspend fun isUsbDebuggingEnabled(): Boolean {
        return withContext(Dispatchers.IO) {
            deviceDataSource.isUsbDebuggingEnabled()
        }
    }

    override suspend fun isUnknownSourcesEnabled(): Boolean {
        return withContext(Dispatchers.IO) {
            deviceDataSource.isUnknownSourcesEnabled()
        }
    }

    private fun mapEntityToStatus(entity: DeviceScanResultEntity): DeviceSecurityStatus {
        // Reconstruct security issues from entity data
        val issues = mutableListOf<SecurityIssue>()

        if (entity.isRooted) {
            issues.add(SecurityIssue("Device is Rooted", "Security risk", IssueSeverity.CRITICAL, "Consider unrooting"))
        }
        if (!entity.isEncrypted) {
            issues.add(SecurityIssue("Not Encrypted", "Data at risk", IssueSeverity.CRITICAL, "Enable encryption"))
        }
        if (!entity.hasScreenLock) {
            issues.add(SecurityIssue("No Screen Lock", "Device unsecured", IssueSeverity.CRITICAL, "Set screen lock"))
        }
        if (entity.isDeveloperOptionsEnabled) {
            issues.add(SecurityIssue("Developer Options", "Debugging enabled", IssueSeverity.WARNING, "Disable if not needed"))
        }
        if (entity.isUsbDebuggingEnabled) {
            issues.add(SecurityIssue("USB Debugging", "ADB access enabled", IssueSeverity.WARNING, "Disable when not developing"))
        }
        if (entity.isUnknownSourcesEnabled) {
            issues.add(SecurityIssue("Unknown Sources", "Side-loading enabled", IssueSeverity.WARNING, "Disable unknown sources"))
        }

        return DeviceSecurityStatus(
            isRooted = entity.isRooted,
            isEncrypted = entity.isEncrypted,
            hasScreenLock = entity.hasScreenLock,
            screenLockType = ScreenLockType.UNKNOWN,
            isDeveloperOptionsEnabled = entity.isDeveloperOptionsEnabled,
            isUsbDebuggingEnabled = entity.isUsbDebuggingEnabled,
            isUnknownSourcesEnabled = entity.isUnknownSourcesEnabled,
            securityPatchLevel = entity.securityPatchLevel,
            androidVersion = entity.androidVersion,
            sdkVersion = 0,
            deviceModel = entity.deviceModel,
            manufacturer = "",
            overallScore = entity.overallScore,
            securityIssues = issues
        )
    }
}
