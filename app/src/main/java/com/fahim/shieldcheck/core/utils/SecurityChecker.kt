package com.fahim.shieldcheck.core.utils

import android.app.KeyguardManager
import android.app.admin.DevicePolicyManager
import android.content.Context
import android.os.Build
import android.provider.Settings
import com.fahim.shieldcheck.core.constants.SecurityConstants
import com.fahim.shieldcheck.domain.model.device.IssueSeverity
import com.fahim.shieldcheck.domain.model.device.ScreenLockType
import com.fahim.shieldcheck.domain.model.device.SecurityIssue
import dagger.hilt.android.qualifiers.ApplicationContext
import java.io.File
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class SecurityChecker @Inject constructor(
    @ApplicationContext private val context: Context
) {

    fun isDeviceRooted(): Boolean {
        return checkRootPaths() || checkRootPackages() || checkSuBinary()
    }

    private fun checkRootPaths(): Boolean {
        return SecurityConstants.ROOT_PATHS.any { path ->
            File(path).exists()
        }
    }

    private fun checkRootPackages(): Boolean {
        val packageManager = context.packageManager
        return SecurityConstants.ROOT_PACKAGES.any { packageName ->
            try {
                packageManager.getPackageInfo(packageName, 0)
                true
            } catch (e: Exception) {
                false
            }
        }
    }

    private fun checkSuBinary(): Boolean {
        return try {
            val process = Runtime.getRuntime().exec(arrayOf("/system/xbin/which", "su"))
            val reader = process.inputStream.bufferedReader()
            val result = reader.readLine()
            reader.close()
            result != null
        } catch (e: Exception) {
            false
        }
    }

    fun isDeviceEncrypted(): Boolean {
        val devicePolicyManager = context.getSystemService(Context.DEVICE_POLICY_SERVICE) as DevicePolicyManager
        return devicePolicyManager.storageEncryptionStatus == DevicePolicyManager.ENCRYPTION_STATUS_ACTIVE ||
                devicePolicyManager.storageEncryptionStatus == DevicePolicyManager.ENCRYPTION_STATUS_ACTIVE_PER_USER
    }

    fun hasScreenLock(): Boolean {
        val keyguardManager = context.getSystemService(Context.KEYGUARD_SERVICE) as KeyguardManager
        return keyguardManager.isKeyguardSecure
    }

    fun getScreenLockType(): ScreenLockType {
        val keyguardManager = context.getSystemService(Context.KEYGUARD_SERVICE) as KeyguardManager

        return when {
            !keyguardManager.isKeyguardSecure -> ScreenLockType.NONE
            keyguardManager.isDeviceSecure -> {
                // Device has secure lock (PIN, Pattern, Password, or Biometric)
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                    ScreenLockType.BIOMETRIC // Could be biometric or other secure method
                } else {
                    ScreenLockType.UNKNOWN
                }
            }
            else -> ScreenLockType.SWIPE
        }
    }

    fun isDeveloperOptionsEnabled(): Boolean {
        return try {
            Settings.Secure.getInt(
                context.contentResolver,
                Settings.Global.DEVELOPMENT_SETTINGS_ENABLED,
                0
            ) == 1
        } catch (e: Exception) {
            false
        }
    }

    fun isUsbDebuggingEnabled(): Boolean {
        return try {
            Settings.Secure.getInt(
                context.contentResolver,
                Settings.Global.ADB_ENABLED,
                0
            ) == 1
        } catch (e: Exception) {
            false
        }
    }

    fun isUnknownSourcesEnabled(): Boolean {
        return try {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                // For Android 8.0+, need to check per-app permission
                // This is a simplified check
                false
            } else {
                @Suppress("DEPRECATION")
                Settings.Secure.getInt(
                    context.contentResolver,
                    Settings.Secure.INSTALL_NON_MARKET_APPS,
                    0
                ) == 1
            }
        } catch (e: Exception) {
            false
        }
    }

    fun getSecurityPatchLevel(): String? {
        return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            Build.VERSION.SECURITY_PATCH
        } else {
            null
        }
    }

    fun calculateDeviceSecurityScore(
        isRooted: Boolean,
        isEncrypted: Boolean,
        hasScreenLock: Boolean,
        isDeveloperOptionsEnabled: Boolean,
        isUsbDebuggingEnabled: Boolean,
        isUnknownSourcesEnabled: Boolean
    ): Int {
        var score = 100

        if (isRooted) score -= SecurityConstants.ROOT_DETECTION_WEIGHT
        if (!isEncrypted) score -= SecurityConstants.ENCRYPTION_WEIGHT
        if (!hasScreenLock) score -= SecurityConstants.SCREEN_LOCK_WEIGHT
        if (isDeveloperOptionsEnabled) score -= SecurityConstants.DEVELOPER_OPTIONS_WEIGHT
        if (isUsbDebuggingEnabled) score -= SecurityConstants.USB_DEBUGGING_WEIGHT
        if (isUnknownSourcesEnabled) score -= SecurityConstants.UNKNOWN_SOURCES_WEIGHT

        return score.coerceIn(0, 100)
    }

    fun identifySecurityIssues(
        isRooted: Boolean,
        isEncrypted: Boolean,
        hasScreenLock: Boolean,
        isDeveloperOptionsEnabled: Boolean,
        isUsbDebuggingEnabled: Boolean,
        isUnknownSourcesEnabled: Boolean
    ): List<SecurityIssue> {
        val issues = mutableListOf<SecurityIssue>()

        if (isRooted) {
            issues.add(
                SecurityIssue(
                    title = "Device is Rooted",
                    description = "Your device appears to be rooted, which can expose it to security vulnerabilities.",
                    severity = IssueSeverity.CRITICAL,
                    recommendation = "Consider unrooting your device for better security, or ensure you only use trusted root applications."
                )
            )
        }

        if (!isEncrypted) {
            issues.add(
                SecurityIssue(
                    title = "Storage Not Encrypted",
                    description = "Your device storage is not encrypted. Data could be accessed if the device is lost or stolen.",
                    severity = IssueSeverity.CRITICAL,
                    recommendation = "Enable device encryption in Settings > Security > Encryption."
                )
            )
        }

        if (!hasScreenLock) {
            issues.add(
                SecurityIssue(
                    title = "No Screen Lock",
                    description = "Your device doesn't have a secure screen lock enabled.",
                    severity = IssueSeverity.CRITICAL,
                    recommendation = "Set up a PIN, password, pattern, or biometric lock in Settings > Security."
                )
            )
        }

        if (isDeveloperOptionsEnabled) {
            issues.add(
                SecurityIssue(
                    title = "Developer Options Enabled",
                    description = "Developer options are enabled, which can expose debugging features.",
                    severity = IssueSeverity.WARNING,
                    recommendation = "Disable developer options if not needed for development work."
                )
            )
        }

        if (isUsbDebuggingEnabled) {
            issues.add(
                SecurityIssue(
                    title = "USB Debugging Enabled",
                    description = "USB debugging is enabled, allowing computer access to your device.",
                    severity = IssueSeverity.WARNING,
                    recommendation = "Disable USB debugging when not actively developing."
                )
            )
        }

        if (isUnknownSourcesEnabled) {
            issues.add(
                SecurityIssue(
                    title = "Unknown Sources Enabled",
                    description = "Installation from unknown sources is enabled, increasing malware risk.",
                    severity = IssueSeverity.WARNING,
                    recommendation = "Disable unknown sources and only install apps from trusted stores."
                )
            )
        }

        return issues
    }
}
