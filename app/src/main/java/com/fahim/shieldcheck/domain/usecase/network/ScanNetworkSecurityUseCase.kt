package com.fahim.shieldcheck.domain.usecase.network

import com.fahim.shieldcheck.domain.model.network.NetworkSecurityStatus
import com.fahim.shieldcheck.domain.repository.NetworkRepository
import javax.inject.Inject

class ScanNetworkSecurityUseCase @Inject constructor(
    private val networkRepository: NetworkRepository
) {
    suspend operator fun invoke(): NetworkSecurityStatus {
        val status = networkRepository.scanNetworkSecurity()
        networkRepository.saveNetworkScanResult(status)
        return status
    }
}
