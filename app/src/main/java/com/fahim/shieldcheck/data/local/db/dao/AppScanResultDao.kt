package com.fahim.shieldcheck.data.local.db.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.fahim.shieldcheck.data.local.db.entity.AppScanResultEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface AppScanResultDao {

    @Query("SELECT * FROM app_scan_results ORDER BY riskScore DESC")
    fun getAllAppScanResults(): Flow<List<AppScanResultEntity>>

    @Query("SELECT * FROM app_scan_results WHERE packageName = :packageName")
    suspend fun getAppScanResultByPackage(packageName: String): AppScanResultEntity?

    @Query("SELECT * FROM app_scan_results WHERE riskLevel = :riskLevel ORDER BY riskScore DESC")
    fun getAppsByRiskLevel(riskLevel: String): Flow<List<AppScanResultEntity>>

    @Query("SELECT * FROM app_scan_results WHERE isSystemApp = :isSystemApp ORDER BY riskScore DESC")
    fun getAppsBySystemStatus(isSystemApp: Boolean): Flow<List<AppScanResultEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAppScanResult(result: AppScanResultEntity): Long

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAllAppScanResults(results: List<AppScanResultEntity>): List<Long>

    @Query("DELETE FROM app_scan_results WHERE packageName = :packageName")
    suspend fun deleteAppScanResult(packageName: String): Int

    @Query("DELETE FROM app_scan_results")
    suspend fun deleteAllAppScanResults(): Int

    @Query("SELECT COUNT(*) FROM app_scan_results")
    suspend fun getAppCount(): Int

    @Query("SELECT AVG(riskScore) FROM app_scan_results")
    suspend fun getAverageRiskScore(): Double?

    @Query("SELECT COUNT(*) FROM app_scan_results WHERE riskLevel = :riskLevel")
    suspend fun getCountByRiskLevel(riskLevel: String): Int
}
