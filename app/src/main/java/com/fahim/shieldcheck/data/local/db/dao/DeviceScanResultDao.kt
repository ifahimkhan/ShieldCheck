package com.fahim.shieldcheck.data.local.db.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.fahim.shieldcheck.data.local.db.entity.DeviceScanResultEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface DeviceScanResultDao {

    @Query("SELECT * FROM device_scan_results ORDER BY scanDate DESC LIMIT 1")
    fun getLatestDeviceScanResult(): Flow<DeviceScanResultEntity?>

    @Query("SELECT * FROM device_scan_results ORDER BY scanDate DESC")
    fun getAllDeviceScanResults(): Flow<List<DeviceScanResultEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertDeviceScanResult(result: DeviceScanResultEntity): Long

    @Query("DELETE FROM device_scan_results WHERE id NOT IN (SELECT id FROM device_scan_results ORDER BY scanDate DESC LIMIT :keepCount)")
    suspend fun deleteOldResults(keepCount: Int): Int

    @Query("DELETE FROM device_scan_results")
    suspend fun deleteAllDeviceScanResults(): Int
}
