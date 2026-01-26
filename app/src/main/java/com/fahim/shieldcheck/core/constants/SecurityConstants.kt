package com.fahim.shieldcheck.core.constants

object SecurityConstants {

    // Risk level thresholds
    const val CRITICAL_THRESHOLD = 75
    const val HIGH_THRESHOLD = 50
    const val MEDIUM_THRESHOLD = 25
    const val LOW_THRESHOLD = 10

    // Security score weights
    const val ROOT_DETECTION_WEIGHT = 25
    const val ENCRYPTION_WEIGHT = 20
    const val SCREEN_LOCK_WEIGHT = 20
    const val DEVELOPER_OPTIONS_WEIGHT = 10
    const val USB_DEBUGGING_WEIGHT = 10
    const val UNKNOWN_SOURCES_WEIGHT = 15

    // Network security
    val COMMON_DANGEROUS_PORTS = listOf(
        21,    // FTP
        22,    // SSH
        23,    // Telnet
        25,    // SMTP
        53,    // DNS
        80,    // HTTP
        110,   // POP3
        135,   // MSRPC
        139,   // NetBIOS
        143,   // IMAP
        443,   // HTTPS
        445,   // SMB
        993,   // IMAPS
        995,   // POP3S
        1433,  // MSSQL
        1521,  // Oracle
        3306,  // MySQL
        3389,  // RDP
        5432,  // PostgreSQL
        5900,  // VNC
        6379,  // Redis
        8080,  // HTTP Alt
        8443,  // HTTPS Alt
        27017  // MongoDB
    )

    val SECURE_WIFI_TYPES = setOf(
        "WPA2",
        "WPA3",
        "WPA2-PSK",
        "WPA3-PSK",
        "WPA2-Enterprise",
        "WPA3-Enterprise"
    )

    val INSECURE_WIFI_TYPES = setOf(
        "WEP",
        "Open",
        "None",
        "OPEN"
    )

    // Root detection paths
    val ROOT_PATHS = listOf(
        "/system/app/Superuser.apk",
        "/sbin/su",
        "/system/bin/su",
        "/system/xbin/su",
        "/data/local/xbin/su",
        "/data/local/bin/su",
        "/system/sd/xbin/su",
        "/system/bin/failsafe/su",
        "/data/local/su",
        "/su/bin/su",
        "/magisk/.core"
    )

    val ROOT_PACKAGES = listOf(
        "com.topjohnwu.magisk",
        "com.koushikdutta.superuser",
        "com.noshufou.android.su",
        "com.thirdparty.superuser",
        "eu.chainfire.supersu",
        "com.yellowes.su",
        "com.kingroot.kinguser",
        "com.kingo.root"
    )

    // Overall security grade thresholds
    const val GRADE_A_THRESHOLD = 90
    const val GRADE_B_THRESHOLD = 75
    const val GRADE_C_THRESHOLD = 60
    const val GRADE_D_THRESHOLD = 40
    // Below D is F
}
