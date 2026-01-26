package com.fahim.shieldcheck.data.local.db.entity

import androidx.room.Entity
import androidx.room.PrimaryKey
import java.util.Date

@Entity(tableName = "app_scan_results")
data class AppScanResultEntity(
    @PrimaryKey
    val packageName: String,
    val appName: String,
    val versionName: String?,
    val versionCode: Long,
    val isSystemApp: Boolean,
    val installedDate: Date?,
    val lastUpdatedDate: Date?,
    val permissions: List<String>,
    val dangerousPermissions: List<String>,
    val riskScore: Int,
    val riskLevel: String,
    val lastScanDate: Date
)
