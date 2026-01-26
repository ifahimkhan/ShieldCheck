package com.fahim.shieldcheck.data.local.db.entity

import androidx.room.Entity
import androidx.room.PrimaryKey
import java.util.Date

@Entity(tableName = "network_scan_results")
data class NetworkScanResultEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val wifiSsid: String?,
    val wifiSecurityType: String?,
    val isWifiSecure: Boolean,
    val openPorts: List<Int>,
    val activeConnections: Int,
    val isVpnActive: Boolean,
    val localIpAddress: String?,
    val overallScore: Int,
    val scanDate: Date
)
