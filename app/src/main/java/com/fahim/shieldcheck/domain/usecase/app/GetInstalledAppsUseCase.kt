package com.fahim.shieldcheck.domain.usecase.app

import com.fahim.shieldcheck.domain.model.app.InstalledApp
import com.fahim.shieldcheck.domain.repository.AppRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class GetInstalledAppsUseCase @Inject constructor(
    private val appRepository: AppRepository
) {
    operator fun invoke(): Flow<List<InstalledApp>> {
        return appRepository.getInstalledApps()
    }

    fun getCached(): Flow<List<InstalledApp>> {
        return appRepository.getCachedApps()
    }

    fun getUserApps(): Flow<List<InstalledApp>> {
        return appRepository.getUserApps()
    }

    fun getSystemApps(): Flow<List<InstalledApp>> {
        return appRepository.getSystemApps()
    }
}
