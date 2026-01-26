package com.fahim.shieldcheck.data.repository

import com.fahim.shieldcheck.core.utils.NetworkAnalyzer
import com.fahim.shieldcheck.data.local.datasource.NetworkDataSource
import com.fahim.shieldcheck.data.local.db.dao.NetworkScanResultDao
import com.fahim.shieldcheck.data.local.db.entity.NetworkScanResultEntity
import com.fahim.shieldcheck.domain.model.network.NetworkIssue
import com.fahim.shieldcheck.domain.model.network.NetworkIssueSeverity
import com.fahim.shieldcheck.domain.model.network.NetworkSecurityStatus
import com.fahim.shieldcheck.domain.model.network.OpenPort
import com.fahim.shieldcheck.domain.model.network.WifiSecurityInfo
import com.fahim.shieldcheck.domain.model.network.WifiSecurityType
import com.fahim.shieldcheck.domain.repository.NetworkRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.withContext
import java.util.Date
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class NetworkRepositoryImpl @Inject constructor(
    private val networkDataSource: NetworkDataSource,
    private val networkAnalyzer: NetworkAnalyzer,
    private val networkScanResultDao: NetworkScanResultDao
) : NetworkRepository {

    override suspend fun scanNetworkSecurity(): NetworkSecurityStatus {
        return withContext(Dispatchers.IO) {
            val wifiInfo = networkDataSource.getWifiSecurityInfo()
            val openPorts = networkDataSource.scanCommonPorts()
            val isVpnActive = networkDataSource.isVpnActive()
            val localIp = networkDataSource.getLocalIpAddress()
            val connections = networkDataSource.getActiveConnections()

            val score = networkAnalyzer.calculateNetworkScore(wifiInfo, openPorts, isVpnActive)
            val issues = networkAnalyzer.identifyNetworkIssues(wifiInfo, openPorts, isVpnActive)

            NetworkSecurityStatus(
                wifiInfo = wifiInfo,
                openPorts = openPorts,
                activeConnections = connections,
                isVpnActive = isVpnActive,
                localIpAddress = localIp,
                overallScore = score,
                issues = issues
            )
        }
    }

    override fun getLatestNetworkScanResult(): Flow<NetworkSecurityStatus?> {
        return networkScanResultDao.getLatestNetworkScanResult().map { entity ->
            entity?.let { mapEntityToStatus(it) }
        }
    }

    override suspend fun saveNetworkScanResult(status: NetworkSecurityStatus) {
        withContext(Dispatchers.IO) {
            val entity = NetworkScanResultEntity(
                wifiSsid = status.wifiInfo?.ssid,
                wifiSecurityType = status.wifiInfo?.securityType?.name,
                isWifiSecure = status.wifiInfo?.isSecure ?: true,
                openPorts = status.openPorts.map { it.port },
                activeConnections = status.activeConnections.size,
                isVpnActive = status.isVpnActive,
                localIpAddress = status.localIpAddress,
                overallScore = status.overallScore,
                scanDate = Date()
            )
            networkScanResultDao.insertNetworkScanResult(entity)
            networkScanResultDao.deleteOldResults(10)
        }
    }

    override suspend fun getWifiSecurityInfo(): WifiSecurityInfo? {
        return withContext(Dispatchers.IO) {
            networkDataSource.getWifiSecurityInfo()
        }
    }

    override suspend fun scanOpenPorts(): List<OpenPort> {
        return withContext(Dispatchers.IO) {
            networkDataSource.scanCommonPorts()
        }
    }

    override suspend fun isVpnActive(): Boolean {
        return withContext(Dispatchers.IO) {
            networkDataSource.isVpnActive()
        }
    }

    override suspend fun getLocalIpAddress(): String? {
        return withContext(Dispatchers.IO) {
            networkDataSource.getLocalIpAddress()
        }
    }

    private fun mapEntityToStatus(entity: NetworkScanResultEntity): NetworkSecurityStatus {
        val wifiInfo = entity.wifiSsid?.let {
            WifiSecurityInfo(
                ssid = it,
                bssid = null,
                securityType = entity.wifiSecurityType?.let { type ->
                    try { WifiSecurityType.valueOf(type) } catch (e: Exception) { WifiSecurityType.UNKNOWN }
                } ?: WifiSecurityType.UNKNOWN,
                signalStrength = 0,
                frequency = 0,
                linkSpeed = 0,
                isSecure = entity.isWifiSecure
            )
        }

        val openPorts = entity.openPorts.map { port ->
            networkAnalyzer.analyzeOpenPort(port)
        }

        // Reconstruct issues
        val issues = mutableListOf<NetworkIssue>()
        if (!entity.isWifiSecure) {
            issues.add(NetworkIssue("WiFi Security Issue", "Previous scan detected insecure WiFi", NetworkIssueSeverity.WARNING, "Check WiFi security"))
        }
        if (entity.openPorts.isNotEmpty()) {
            issues.add(NetworkIssue("Open Ports Detected", "${entity.openPorts.size} open ports found", NetworkIssueSeverity.INFO, "Review open ports"))
        }

        return NetworkSecurityStatus(
            wifiInfo = wifiInfo,
            openPorts = openPorts,
            activeConnections = emptyList(),
            isVpnActive = entity.isVpnActive,
            localIpAddress = entity.localIpAddress,
            overallScore = entity.overallScore,
            issues = issues
        )
    }
}
