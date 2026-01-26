package com.fahim.shieldcheck.core.constants

import android.Manifest

object PermissionConstants {

    // Critical permissions (25 points)
    val CRITICAL_PERMISSIONS = setOf(
        Manifest.permission.READ_SMS,
        Manifest.permission.SEND_SMS,
        Manifest.permission.RECEIVE_SMS,
        Manifest.permission.READ_CALL_LOG,
        Manifest.permission.WRITE_CALL_LOG,
        Manifest.permission.READ_CONTACTS,
        Manifest.permission.WRITE_CONTACTS,
        "android.permission.REQUEST_INSTALL_PACKAGES",
        "android.permission.INSTALL_PACKAGES",
        "android.permission.DELETE_PACKAGES",
        Manifest.permission.READ_PHONE_STATE,
        Manifest.permission.CALL_PHONE,
        Manifest.permission.PROCESS_OUTGOING_CALLS,
        "android.permission.BIND_ACCESSIBILITY_SERVICE",
        "android.permission.BIND_DEVICE_ADMIN",
        "android.permission.SYSTEM_ALERT_WINDOW"
    )

    // High risk permissions (15 points)
    val HIGH_RISK_PERMISSIONS = setOf(
        Manifest.permission.ACCESS_FINE_LOCATION,
        Manifest.permission.ACCESS_COARSE_LOCATION,
        Manifest.permission.ACCESS_BACKGROUND_LOCATION,
        Manifest.permission.CAMERA,
        Manifest.permission.RECORD_AUDIO,
        Manifest.permission.READ_EXTERNAL_STORAGE,
        Manifest.permission.WRITE_EXTERNAL_STORAGE,
        "android.permission.READ_MEDIA_IMAGES",
        "android.permission.READ_MEDIA_VIDEO",
        "android.permission.READ_MEDIA_AUDIO",
        "android.permission.MANAGE_EXTERNAL_STORAGE",
        Manifest.permission.GET_ACCOUNTS,
        "android.permission.BODY_SENSORS",
        "android.permission.ACTIVITY_RECOGNITION"
    )

    // Medium risk permissions (8 points)
    val MEDIUM_RISK_PERMISSIONS = setOf(
        Manifest.permission.READ_CALENDAR,
        Manifest.permission.WRITE_CALENDAR,
        Manifest.permission.BLUETOOTH,
        Manifest.permission.BLUETOOTH_ADMIN,
        Manifest.permission.BLUETOOTH_CONNECT,
        Manifest.permission.BLUETOOTH_SCAN,
        Manifest.permission.NFC,
        "android.permission.USE_BIOMETRIC",
        "android.permission.USE_FINGERPRINT",
        Manifest.permission.RECEIVE_BOOT_COMPLETED,
        "android.permission.REQUEST_IGNORE_BATTERY_OPTIMIZATIONS",
        "android.permission.FOREGROUND_SERVICE"
    )

    // Low risk permissions (3 points)
    val LOW_RISK_PERMISSIONS = setOf(
        Manifest.permission.INTERNET,
        Manifest.permission.ACCESS_NETWORK_STATE,
        Manifest.permission.ACCESS_WIFI_STATE,
        Manifest.permission.VIBRATE,
        Manifest.permission.WAKE_LOCK,
        "android.permission.POST_NOTIFICATIONS",
        Manifest.permission.RECEIVE_BOOT_COMPLETED
    )

    // Permission risk scores
    const val CRITICAL_SCORE = 25
    const val HIGH_SCORE = 15
    const val MEDIUM_SCORE = 8
    const val LOW_SCORE = 3

    // Permission descriptions
    val PERMISSION_DESCRIPTIONS = mapOf(
        Manifest.permission.READ_SMS to "Read text messages",
        Manifest.permission.SEND_SMS to "Send text messages",
        Manifest.permission.READ_CALL_LOG to "Read call history",
        Manifest.permission.READ_CONTACTS to "Read contacts",
        Manifest.permission.WRITE_CONTACTS to "Modify contacts",
        Manifest.permission.ACCESS_FINE_LOCATION to "Access precise location",
        Manifest.permission.ACCESS_COARSE_LOCATION to "Access approximate location",
        Manifest.permission.ACCESS_BACKGROUND_LOCATION to "Access location in background",
        Manifest.permission.CAMERA to "Take photos and videos",
        Manifest.permission.RECORD_AUDIO to "Record audio",
        Manifest.permission.READ_EXTERNAL_STORAGE to "Read files from storage",
        Manifest.permission.WRITE_EXTERNAL_STORAGE to "Write files to storage",
        Manifest.permission.READ_CALENDAR to "Read calendar events",
        Manifest.permission.WRITE_CALENDAR to "Modify calendar events",
        Manifest.permission.INTERNET to "Access the internet",
        Manifest.permission.READ_PHONE_STATE to "Read phone status and identity",
        Manifest.permission.CALL_PHONE to "Make phone calls",
        "android.permission.SYSTEM_ALERT_WINDOW" to "Draw over other apps",
        "android.permission.REQUEST_INSTALL_PACKAGES" to "Install apps from unknown sources",
        "android.permission.BIND_ACCESSIBILITY_SERVICE" to "Use accessibility services",
        "android.permission.MANAGE_EXTERNAL_STORAGE" to "Full access to all files"
    )

    fun getPermissionRiskScore(permission: String): Int {
        return when {
            CRITICAL_PERMISSIONS.contains(permission) -> CRITICAL_SCORE
            HIGH_RISK_PERMISSIONS.contains(permission) -> HIGH_SCORE
            MEDIUM_RISK_PERMISSIONS.contains(permission) -> MEDIUM_SCORE
            LOW_RISK_PERMISSIONS.contains(permission) -> LOW_SCORE
            else -> 1 // Unknown permissions get minimal score
        }
    }

    fun getPermissionDescription(permission: String): String {
        return PERMISSION_DESCRIPTIONS[permission]
            ?: permission.substringAfterLast('.').replace('_', ' ').lowercase()
                .replaceFirstChar { it.uppercase() }
    }

    fun isDangerousPermission(permission: String): Boolean {
        return CRITICAL_PERMISSIONS.contains(permission) ||
                HIGH_RISK_PERMISSIONS.contains(permission)
    }
}
