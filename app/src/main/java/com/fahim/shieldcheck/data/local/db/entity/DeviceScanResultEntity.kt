package com.fahim.shieldcheck.data.local.db.entity

import androidx.room.Entity
import androidx.room.PrimaryKey
import java.util.Date

@Entity(tableName = "device_scan_results")
data class DeviceScanResultEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val isRooted: Boolean,
    val isEncrypted: Boolean,
    val hasScreenLock: Boolean,
    val isDeveloperOptionsEnabled: Boolean,
    val isUsbDebuggingEnabled: Boolean,
    val isUnknownSourcesEnabled: Boolean,
    val securityPatchLevel: String?,
    val androidVersion: String,
    val deviceModel: String,
    val overallScore: Int,
    val scanDate: Date
)
