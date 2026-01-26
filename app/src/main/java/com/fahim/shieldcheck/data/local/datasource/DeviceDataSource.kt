package com.fahim.shieldcheck.data.local.datasource

import android.os.Build
import com.fahim.shieldcheck.core.utils.SecurityChecker
import com.fahim.shieldcheck.domain.model.device.DeviceSecurityStatus
import com.fahim.shieldcheck.domain.model.device.ScreenLockType
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class DeviceDataSource @Inject constructor(
    private val securityChecker: SecurityChecker
) {

    fun getDeviceSecurityStatus(): DeviceSecurityStatus {
        val isRooted = securityChecker.isDeviceRooted()
        val isEncrypted = securityChecker.isDeviceEncrypted()
        val hasScreenLock = securityChecker.hasScreenLock()
        val screenLockType = securityChecker.getScreenLockType()
        val isDeveloperOptionsEnabled = securityChecker.isDeveloperOptionsEnabled()
        val isUsbDebuggingEnabled = securityChecker.isUsbDebuggingEnabled()
        val isUnknownSourcesEnabled = securityChecker.isUnknownSourcesEnabled()

        val score = securityChecker.calculateDeviceSecurityScore(
            isRooted = isRooted,
            isEncrypted = isEncrypted,
            hasScreenLock = hasScreenLock,
            isDeveloperOptionsEnabled = isDeveloperOptionsEnabled,
            isUsbDebuggingEnabled = isUsbDebuggingEnabled,
            isUnknownSourcesEnabled = isUnknownSourcesEnabled
        )

        val issues = securityChecker.identifySecurityIssues(
            isRooted = isRooted,
            isEncrypted = isEncrypted,
            hasScreenLock = hasScreenLock,
            isDeveloperOptionsEnabled = isDeveloperOptionsEnabled,
            isUsbDebuggingEnabled = isUsbDebuggingEnabled,
            isUnknownSourcesEnabled = isUnknownSourcesEnabled
        )

        return DeviceSecurityStatus(
            isRooted = isRooted,
            isEncrypted = isEncrypted,
            hasScreenLock = hasScreenLock,
            screenLockType = screenLockType,
            isDeveloperOptionsEnabled = isDeveloperOptionsEnabled,
            isUsbDebuggingEnabled = isUsbDebuggingEnabled,
            isUnknownSourcesEnabled = isUnknownSourcesEnabled,
            securityPatchLevel = securityChecker.getSecurityPatchLevel(),
            androidVersion = Build.VERSION.RELEASE,
            sdkVersion = Build.VERSION.SDK_INT,
            deviceModel = Build.MODEL,
            manufacturer = Build.MANUFACTURER,
            overallScore = score,
            securityIssues = issues
        )
    }

    fun isRooted(): Boolean = securityChecker.isDeviceRooted()
    fun isEncrypted(): Boolean = securityChecker.isDeviceEncrypted()
    fun hasScreenLock(): Boolean = securityChecker.hasScreenLock()
    fun isDeveloperOptionsEnabled(): Boolean = securityChecker.isDeveloperOptionsEnabled()
    fun isUsbDebuggingEnabled(): Boolean = securityChecker.isUsbDebuggingEnabled()
    fun isUnknownSourcesEnabled(): Boolean = securityChecker.isUnknownSourcesEnabled()
}
