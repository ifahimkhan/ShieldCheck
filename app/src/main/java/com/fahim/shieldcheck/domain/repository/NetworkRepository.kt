package com.fahim.shieldcheck.domain.repository

import com.fahim.shieldcheck.domain.model.network.NetworkSecurityStatus
import com.fahim.shieldcheck.domain.model.network.OpenPort
import com.fahim.shieldcheck.domain.model.network.WifiSecurityInfo
import kotlinx.coroutines.flow.Flow

interface NetworkRepository {
    suspend fun scanNetworkSecurity(): NetworkSecurityStatus
    fun getLatestNetworkScanResult(): Flow<NetworkSecurityStatus?>
    suspend fun saveNetworkScanResult(status: NetworkSecurityStatus)
    suspend fun getWifiSecurityInfo(): WifiSecurityInfo?
    suspend fun scanOpenPorts(): List<OpenPort>
    suspend fun isVpnActive(): Boolean
    suspend fun getLocalIpAddress(): String?
}
