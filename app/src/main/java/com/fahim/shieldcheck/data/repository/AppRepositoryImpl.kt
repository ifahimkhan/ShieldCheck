package com.fahim.shieldcheck.data.repository

import com.fahim.shieldcheck.data.local.datasource.AppDataSource
import com.fahim.shieldcheck.data.local.db.dao.AppScanResultDao
import com.fahim.shieldcheck.data.mapper.AppMapper
import com.fahim.shieldcheck.domain.model.app.InstalledApp
import com.fahim.shieldcheck.domain.model.app.RiskLevel
import com.fahim.shieldcheck.domain.repository.AppRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.withContext
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class AppRepositoryImpl @Inject constructor(
    private val appDataSource: AppDataSource,
    private val appScanResultDao: AppScanResultDao,
    private val appMapper: AppMapper
) : AppRepository {

    override fun getInstalledApps(): Flow<List<InstalledApp>> = flow {
        val packages = appDataSource.getInstalledPackages()
        val apps = packages.map { appMapper.mapPackageInfoToInstalledApp(it) }
            .sortedByDescending { it.riskScore }
        emit(apps)
    }.flowOn(Dispatchers.IO)

    override fun getCachedApps(): Flow<List<InstalledApp>> {
        return appScanResultDao.getAllAppScanResults().map { entities ->
            entities.map { entity ->
                val packageInfo = appDataSource.getPackageInfo(entity.packageName)
                val icon = packageInfo?.let { appDataSource.getAppIcon(it) }
                appMapper.mapEntityToInstalledApp(entity, icon)
            }
        }
    }

    override suspend fun getAppByPackage(packageName: String): InstalledApp? {
        return withContext(Dispatchers.IO) {
            // First try to get fresh data from system
            appDataSource.getPackageInfo(packageName)?.let { packageInfo ->
                appMapper.mapPackageInfoToInstalledApp(packageInfo)
            }
        }
    }

    override suspend fun scanAllApps(): List<InstalledApp> {
        return withContext(Dispatchers.IO) {
            val packages = appDataSource.getInstalledPackages()
            val apps = packages.map { appMapper.mapPackageInfoToInstalledApp(it) }

            // Cache results
            val entities = apps.map { appMapper.mapInstalledAppToEntity(it) }
            appScanResultDao.insertAllAppScanResults(entities)

            apps.sortedByDescending { it.riskScore }
        }
    }

    override suspend fun refreshAppCache() {
        withContext(Dispatchers.IO) {
            appScanResultDao.deleteAllAppScanResults()
            scanAllApps()
        }
    }

    override fun getAppsByRiskLevel(riskLevel: RiskLevel): Flow<List<InstalledApp>> {
        return appScanResultDao.getAppsByRiskLevel(riskLevel.name).map { entities ->
            entities.map { entity ->
                val packageInfo = appDataSource.getPackageInfo(entity.packageName)
                val icon = packageInfo?.let { appDataSource.getAppIcon(it) }
                appMapper.mapEntityToInstalledApp(entity, icon)
            }
        }
    }

    override fun getSystemApps(): Flow<List<InstalledApp>> {
        return appScanResultDao.getAppsBySystemStatus(true).map { entities ->
            entities.map { entity ->
                val packageInfo = appDataSource.getPackageInfo(entity.packageName)
                val icon = packageInfo?.let { appDataSource.getAppIcon(it) }
                appMapper.mapEntityToInstalledApp(entity, icon)
            }
        }
    }

    override fun getUserApps(): Flow<List<InstalledApp>> {
        return appScanResultDao.getAppsBySystemStatus(false).map { entities ->
            entities.map { entity ->
                val packageInfo = appDataSource.getPackageInfo(entity.packageName)
                val icon = packageInfo?.let { appDataSource.getAppIcon(it) }
                appMapper.mapEntityToInstalledApp(entity, icon)
            }
        }
    }

    override suspend fun getAverageRiskScore(): Double {
        return withContext(Dispatchers.IO) {
            appScanResultDao.getAverageRiskScore() ?: 0.0
        }
    }

    override suspend fun getAppCountByRiskLevel(riskLevel: RiskLevel): Int {
        return withContext(Dispatchers.IO) {
            appScanResultDao.getCountByRiskLevel(riskLevel.name)
        }
    }
}
