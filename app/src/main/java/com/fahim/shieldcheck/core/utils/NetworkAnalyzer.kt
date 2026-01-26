package com.fahim.shieldcheck.core.utils

import com.fahim.shieldcheck.core.constants.SecurityConstants
import com.fahim.shieldcheck.domain.model.network.NetworkIssue
import com.fahim.shieldcheck.domain.model.network.NetworkIssueSeverity
import com.fahim.shieldcheck.domain.model.network.OpenPort
import com.fahim.shieldcheck.domain.model.network.WifiSecurityInfo
import com.fahim.shieldcheck.domain.model.network.WifiSecurityType
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class NetworkAnalyzer @Inject constructor() {

    fun analyzeOpenPort(port: Int): OpenPort {
        val serviceName = getServiceName(port)
        val isExploited = SecurityConstants.COMMON_DANGEROUS_PORTS.contains(port)

        return OpenPort(
            port = port,
            protocol = "TCP",
            serviceName = serviceName,
            isCommonlyExploited = isExploited
        )
    }

    private fun getServiceName(port: Int): String {
        return when (port) {
            21 -> "FTP"
            22 -> "SSH"
            23 -> "Telnet"
            25 -> "SMTP"
            53 -> "DNS"
            80 -> "HTTP"
            110 -> "POP3"
            135 -> "MSRPC"
            139 -> "NetBIOS"
            143 -> "IMAP"
            443 -> "HTTPS"
            445 -> "SMB"
            993 -> "IMAPS"
            995 -> "POP3S"
            1433 -> "MSSQL"
            1521 -> "Oracle"
            3306 -> "MySQL"
            3389 -> "RDP"
            5432 -> "PostgreSQL"
            5900 -> "VNC"
            6379 -> "Redis"
            8080 -> "HTTP Proxy"
            8443 -> "HTTPS Alt"
            27017 -> "MongoDB"
            else -> "Unknown"
        }
    }

    fun calculateNetworkScore(
        wifiInfo: WifiSecurityInfo?,
        openPorts: List<OpenPort>,
        isVpnActive: Boolean
    ): Int {
        var score = 100

        // WiFi security check
        wifiInfo?.let {
            if (!it.isSecure) {
                score -= when (it.securityType) {
                    WifiSecurityType.OPEN -> 40
                    WifiSecurityType.WEP -> 30
                    WifiSecurityType.WPA -> 15
                    else -> 0
                }
            }
        } ?: run {
            // No WiFi connected - neutral
        }

        // Open ports penalty
        val dangerousPortCount = openPorts.count { it.isCommonlyExploited }
        score -= (dangerousPortCount * 5).coerceAtMost(30)

        // VPN bonus
        if (isVpnActive) {
            score = (score + 10).coerceAtMost(100)
        }

        return score.coerceIn(0, 100)
    }

    fun identifyNetworkIssues(
        wifiInfo: WifiSecurityInfo?,
        openPorts: List<OpenPort>,
        isVpnActive: Boolean
    ): List<NetworkIssue> {
        val issues = mutableListOf<NetworkIssue>()

        // WiFi security issues
        wifiInfo?.let {
            when (it.securityType) {
                WifiSecurityType.OPEN -> {
                    issues.add(
                        NetworkIssue(
                            title = "Open WiFi Network",
                            description = "You are connected to an open WiFi network without encryption.",
                            severity = NetworkIssueSeverity.CRITICAL,
                            recommendation = "Avoid transmitting sensitive data or use a VPN."
                        )
                    )
                }
                WifiSecurityType.WEP -> {
                    issues.add(
                        NetworkIssue(
                            title = "Weak WiFi Encryption (WEP)",
                            description = "WEP encryption is outdated and easily cracked.",
                            severity = NetworkIssueSeverity.CRITICAL,
                            recommendation = "Ask the network administrator to upgrade to WPA2 or WPA3."
                        )
                    )
                }
                WifiSecurityType.WPA -> {
                    issues.add(
                        NetworkIssue(
                            title = "Older WiFi Encryption (WPA)",
                            description = "WPA is less secure than WPA2 or WPA3.",
                            severity = NetworkIssueSeverity.WARNING,
                            recommendation = "Consider connecting to a network with WPA2 or WPA3."
                        )
                    )
                }
                else -> {}
            }
        }

        // Open ports issues
        val exploitedPorts = openPorts.filter { it.isCommonlyExploited }
        if (exploitedPorts.isNotEmpty()) {
            issues.add(
                NetworkIssue(
                    title = "Potentially Dangerous Open Ports",
                    description = "Found ${exploitedPorts.size} open ports that are commonly targeted.",
                    severity = NetworkIssueSeverity.WARNING,
                    recommendation = "Review running services and close unnecessary ports."
                )
            )
        }

        // VPN recommendation
        if (!isVpnActive && wifiInfo != null) {
            issues.add(
                NetworkIssue(
                    title = "No VPN Active",
                    description = "Your network traffic is not protected by a VPN.",
                    severity = NetworkIssueSeverity.INFO,
                    recommendation = "Consider using a VPN for additional privacy and security."
                )
            )
        }

        return issues
    }
}
