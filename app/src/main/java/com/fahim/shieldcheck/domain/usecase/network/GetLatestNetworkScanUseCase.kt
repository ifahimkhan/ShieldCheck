package com.fahim.shieldcheck.domain.usecase.network

import com.fahim.shieldcheck.domain.model.network.NetworkSecurityStatus
import com.fahim.shieldcheck.domain.repository.NetworkRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class GetLatestNetworkScanUseCase @Inject constructor(
    private val networkRepository: NetworkRepository
) {
    operator fun invoke(): Flow<NetworkSecurityStatus?> {
        return networkRepository.getLatestNetworkScanResult()
    }
}
