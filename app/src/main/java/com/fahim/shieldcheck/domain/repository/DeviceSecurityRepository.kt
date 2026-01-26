package com.fahim.shieldcheck.domain.repository

import com.fahim.shieldcheck.domain.model.device.DeviceSecurityStatus
import kotlinx.coroutines.flow.Flow

interface DeviceSecurityRepository {
    suspend fun checkDeviceSecurity(): DeviceSecurityStatus
    fun getLatestDeviceScanResult(): Flow<DeviceSecurityStatus?>
    suspend fun saveDeviceScanResult(status: DeviceSecurityStatus)
    suspend fun isDeviceRooted(): Boolean
    suspend fun isDeviceEncrypted(): Boolean
    suspend fun hasScreenLock(): Boolean
    suspend fun isDeveloperOptionsEnabled(): Boolean
    suspend fun isUsbDebuggingEnabled(): Boolean
    suspend fun isUnknownSourcesEnabled(): Boolean
}
