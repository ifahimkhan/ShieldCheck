package com.fahim.shieldcheck.data.local.db.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.fahim.shieldcheck.data.local.db.entity.NetworkScanResultEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface NetworkScanResultDao {

    @Query("SELECT * FROM network_scan_results ORDER BY scanDate DESC LIMIT 1")
    fun getLatestNetworkScanResult(): Flow<NetworkScanResultEntity?>

    @Query("SELECT * FROM network_scan_results ORDER BY scanDate DESC")
    fun getAllNetworkScanResults(): Flow<List<NetworkScanResultEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertNetworkScanResult(result: NetworkScanResultEntity): Long

    @Query("DELETE FROM network_scan_results WHERE id NOT IN (SELECT id FROM network_scan_results ORDER BY scanDate DESC LIMIT :keepCount)")
    suspend fun deleteOldResults(keepCount: Int): Int

    @Query("DELETE FROM network_scan_results")
    suspend fun deleteAllNetworkScanResults(): Int
}
