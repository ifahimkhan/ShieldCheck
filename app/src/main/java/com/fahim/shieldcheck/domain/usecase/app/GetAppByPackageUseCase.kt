package com.fahim.shieldcheck.domain.usecase.app

import com.fahim.shieldcheck.domain.model.app.InstalledApp
import com.fahim.shieldcheck.domain.repository.AppRepository
import javax.inject.Inject

class GetAppByPackageUseCase @Inject constructor(
    private val appRepository: AppRepository
) {
    suspend operator fun invoke(packageName: String): InstalledApp? {
        return appRepository.getAppByPackage(packageName)
    }
}
