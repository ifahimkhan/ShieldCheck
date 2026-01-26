package com.fahim.shieldcheck.domain.repository

import com.fahim.shieldcheck.domain.model.app.InstalledApp
import com.fahim.shieldcheck.domain.model.app.RiskLevel
import kotlinx.coroutines.flow.Flow

interface AppRepository {
    fun getInstalledApps(): Flow<List<InstalledApp>>
    fun getCachedApps(): Flow<List<InstalledApp>>
    suspend fun getAppByPackage(packageName: String): InstalledApp?
    suspend fun scanAllApps(): List<InstalledApp>
    suspend fun refreshAppCache()
    fun getAppsByRiskLevel(riskLevel: RiskLevel): Flow<List<InstalledApp>>
    fun getSystemApps(): Flow<List<InstalledApp>>
    fun getUserApps(): Flow<List<InstalledApp>>
    suspend fun getAverageRiskScore(): Double
    suspend fun getAppCountByRiskLevel(riskLevel: RiskLevel): Int
}
