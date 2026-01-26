package com.fahim.shieldcheck.domain.model.device

data class DeviceSecurityStatus(
    val isRooted: Boolean,
    val isEncrypted: Boolean,
    val hasScreenLock: Boolean,
    val screenLockType: ScreenLockType,
    val isDeveloperOptionsEnabled: Boolean,
    val isUsbDebuggingEnabled: Boolean,
    val isUnknownSourcesEnabled: Boolean,
    val securityPatchLevel: String?,
    val androidVersion: String,
    val sdkVersion: Int,
    val deviceModel: String,
    val manufacturer: String,
    val overallScore: Int,
    val securityIssues: List<SecurityIssue>
) {
    val grade: String
        get() = when {
            overallScore >= 90 -> "A"
            overallScore >= 75 -> "B"
            overallScore >= 60 -> "C"
            overallScore >= 40 -> "D"
            else -> "F"
        }

    val hasSecurityIssues: Boolean
        get() = securityIssues.isNotEmpty()

    val criticalIssuesCount: Int
        get() = securityIssues.count { it.severity == IssueSeverity.CRITICAL }

    val warningIssuesCount: Int
        get() = securityIssues.count { it.severity == IssueSeverity.WARNING }
}

enum class ScreenLockType {
    NONE,
    SWIPE,
    PATTERN,
    PIN,
    PASSWORD,
    BIOMETRIC,
    UNKNOWN
}

data class SecurityIssue(
    val title: String,
    val description: String,
    val severity: IssueSeverity,
    val recommendation: String
)

enum class IssueSeverity {
    CRITICAL,
    WARNING,
    INFO
}
