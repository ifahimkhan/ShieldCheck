package com.fahim.shieldcheck.data.local.datasource

import android.content.Context
import android.net.ConnectivityManager
import android.net.NetworkCapabilities
import android.net.wifi.WifiManager
import android.os.Build
import com.fahim.shieldcheck.core.constants.SecurityConstants
import com.fahim.shieldcheck.domain.model.network.ConnectionState
import com.fahim.shieldcheck.domain.model.network.NetworkConnection
import com.fahim.shieldcheck.domain.model.network.OpenPort
import com.fahim.shieldcheck.domain.model.network.WifiSecurityInfo
import com.fahim.shieldcheck.domain.model.network.WifiSecurityType
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.net.Inet4Address
import java.net.InetSocketAddress
import java.net.NetworkInterface
import java.net.Socket
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class NetworkDataSource @Inject constructor(
    @ApplicationContext private val context: Context
) {

    private val wifiManager: WifiManager by lazy {
        context.applicationContext.getSystemService(Context.WIFI_SERVICE) as WifiManager
    }

    private val connectivityManager: ConnectivityManager by lazy {
        context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
    }

    fun getWifiSecurityInfo(): WifiSecurityInfo? {
        if (!isWifiConnected()) return null

        @Suppress("DEPRECATION")
        val wifiInfo = wifiManager.connectionInfo ?: return null

        val ssid = wifiInfo.ssid?.removeSurrounding("\"")
        val securityType = detectWifiSecurityType()

        return WifiSecurityInfo(
            ssid = ssid,
            bssid = wifiInfo.bssid,
            securityType = securityType,
            signalStrength = WifiManager.calculateSignalLevel(wifiInfo.rssi, 5),
            frequency = wifiInfo.frequency,
            linkSpeed = wifiInfo.linkSpeed,
            isSecure = securityType.isSecure
        )
    }

    private fun detectWifiSecurityType(): WifiSecurityType {
        // This is a simplified detection - in production you'd use WifiConfiguration
        // or scan results for accurate security type detection
        return try {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                // For Android 10+, we can't easily get security type without location permission
                // Default to WPA2 as most networks use it
                WifiSecurityType.WPA2
            } else {
                @Suppress("DEPRECATION")
                val configuredNetworks = wifiManager.configuredNetworks
                val currentBssid = wifiManager.connectionInfo?.bssid

                val currentConfig = configuredNetworks?.find { it.BSSID == currentBssid }

                when {
                    currentConfig?.allowedKeyManagement?.get(android.net.wifi.WifiConfiguration.KeyMgmt.WPA_PSK) == true ->
                        WifiSecurityType.WPA2
                    currentConfig?.allowedKeyManagement?.get(android.net.wifi.WifiConfiguration.KeyMgmt.WPA_EAP) == true ->
                        WifiSecurityType.WPA2
                    currentConfig?.allowedKeyManagement?.get(android.net.wifi.WifiConfiguration.KeyMgmt.NONE) == true ->
                        if (currentConfig.wepKeys?.any { it != null } == true) WifiSecurityType.WEP
                        else WifiSecurityType.OPEN
                    else -> WifiSecurityType.UNKNOWN
                }
            }
        } catch (e: Exception) {
            WifiSecurityType.UNKNOWN
        }
    }

    fun isWifiConnected(): Boolean {
        val network = connectivityManager.activeNetwork ?: return false
        val capabilities = connectivityManager.getNetworkCapabilities(network) ?: return false
        return capabilities.hasTransport(NetworkCapabilities.TRANSPORT_WIFI)
    }

    suspend fun scanCommonPorts(): List<OpenPort> = withContext(Dispatchers.IO) {
        val openPorts = mutableListOf<OpenPort>()
        val localIp = getLocalIpAddress() ?: return@withContext openPorts

        // Scan a subset of common ports to avoid long scan times
        val portsToScan = listOf(22, 80, 443, 8080, 3000, 5000, 8000)

        portsToScan.forEach { port ->
            try {
                Socket().use { socket ->
                    socket.connect(InetSocketAddress(localIp, port), 100)
                    openPorts.add(
                        OpenPort(
                            port = port,
                            protocol = "TCP",
                            serviceName = getServiceName(port),
                            isCommonlyExploited = SecurityConstants.COMMON_DANGEROUS_PORTS.contains(port)
                        )
                    )
                }
            } catch (e: Exception) {
                // Port is closed or filtered - this is expected for most ports
            }
        }

        openPorts
    }

    private fun getServiceName(port: Int): String {
        return when (port) {
            22 -> "SSH"
            80 -> "HTTP"
            443 -> "HTTPS"
            3000 -> "Dev Server"
            5000 -> "Dev Server"
            8000 -> "Dev Server"
            8080 -> "HTTP Proxy"
            else -> "Unknown"
        }
    }

    fun isVpnActive(): Boolean {
        val network = connectivityManager.activeNetwork ?: return false
        val capabilities = connectivityManager.getNetworkCapabilities(network) ?: return false
        return capabilities.hasTransport(NetworkCapabilities.TRANSPORT_VPN)
    }

    fun getLocalIpAddress(): String? {
        return try {
            NetworkInterface.getNetworkInterfaces()?.toList()?.flatMap { networkInterface ->
                networkInterface.inetAddresses.toList()
                    .filter { !it.isLoopbackAddress && it is Inet4Address }
                    .map { it.hostAddress }
            }?.firstOrNull()
        } catch (e: Exception) {
            null
        }
    }

    fun getActiveConnections(): List<NetworkConnection> {
        // This would require reading /proc/net/tcp which needs root or special permissions
        // For now, return an empty list as a placeholder
        return emptyList()
    }
}
