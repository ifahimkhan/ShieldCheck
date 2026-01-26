package com.fahim.shieldcheck.domain.usecase.app

import com.fahim.shieldcheck.domain.model.app.InstalledApp
import com.fahim.shieldcheck.domain.repository.AppRepository
import javax.inject.Inject

class ScanAppsUseCase @Inject constructor(
    private val appRepository: AppRepository
) {
    suspend operator fun invoke(): List<InstalledApp> {
        return appRepository.scanAllApps()
    }

    suspend fun refreshCache() {
        appRepository.refreshAppCache()
    }
}
