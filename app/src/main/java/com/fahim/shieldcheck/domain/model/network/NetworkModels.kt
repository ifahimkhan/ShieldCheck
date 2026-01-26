package com.fahim.shieldcheck.domain.model.network

data class NetworkSecurityStatus(
    val wifiInfo: WifiSecurityInfo?,
    val openPorts: List<OpenPort>,
    val activeConnections: List<NetworkConnection>,
    val isVpnActive: Boolean,
    val localIpAddress: String?,
    val overallScore: Int,
    val issues: List<NetworkIssue>
) {
    val hasIssues: Boolean
        get() = issues.isNotEmpty()

    val grade: String
        get() = when {
            overallScore >= 90 -> "A"
            overallScore >= 75 -> "B"
            overallScore >= 60 -> "C"
            overallScore >= 40 -> "D"
            else -> "F"
        }
}

data class WifiSecurityInfo(
    val ssid: String?,
    val bssid: String?,
    val securityType: WifiSecurityType,
    val signalStrength: Int,
    val frequency: Int,
    val linkSpeed: Int,
    val isSecure: Boolean
)

enum class WifiSecurityType {
    OPEN,
    WEP,
    WPA,
    WPA2,
    WPA3,
    UNKNOWN;

    val isSecure: Boolean
        get() = this == WPA2 || this == WPA3

    val displayName: String
        get() = when (this) {
            OPEN -> "Open (No Security)"
            WEP -> "WEP (Weak)"
            WPA -> "WPA"
            WPA2 -> "WPA2"
            WPA3 -> "WPA3"
            UNKNOWN -> "Unknown"
        }
}

data class OpenPort(
    val port: Int,
    val protocol: String,
    val serviceName: String,
    val isCommonlyExploited: Boolean
)

data class NetworkConnection(
    val localAddress: String,
    val localPort: Int,
    val remoteAddress: String,
    val remotePort: Int,
    val state: ConnectionState,
    val protocol: String
)

enum class ConnectionState {
    ESTABLISHED,
    LISTEN,
    TIME_WAIT,
    CLOSE_WAIT,
    UNKNOWN
}

data class NetworkIssue(
    val title: String,
    val description: String,
    val severity: NetworkIssueSeverity,
    val recommendation: String
)

enum class NetworkIssueSeverity {
    CRITICAL,
    WARNING,
    INFO
}
