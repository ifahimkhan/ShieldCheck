package com.fahim.shieldcheck.domain.usecase.device

import com.fahim.shieldcheck.domain.model.device.DeviceSecurityStatus
import com.fahim.shieldcheck.domain.repository.DeviceSecurityRepository
import javax.inject.Inject

class CheckDeviceSecurityUseCase @Inject constructor(
    private val deviceSecurityRepository: DeviceSecurityRepository
) {
    suspend operator fun invoke(): DeviceSecurityStatus {
        val status = deviceSecurityRepository.checkDeviceSecurity()
        deviceSecurityRepository.saveDeviceScanResult(status)
        return status
    }
}
