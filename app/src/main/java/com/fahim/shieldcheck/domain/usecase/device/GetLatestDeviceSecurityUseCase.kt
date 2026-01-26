package com.fahim.shieldcheck.domain.usecase.device

import com.fahim.shieldcheck.domain.model.device.DeviceSecurityStatus
import com.fahim.shieldcheck.domain.repository.DeviceSecurityRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class GetLatestDeviceSecurityUseCase @Inject constructor(
    private val deviceSecurityRepository: DeviceSecurityRepository
) {
    operator fun invoke(): Flow<DeviceSecurityStatus?> {
        return deviceSecurityRepository.getLatestDeviceScanResult()
    }
}
