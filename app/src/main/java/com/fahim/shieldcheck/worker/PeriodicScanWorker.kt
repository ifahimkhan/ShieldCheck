package com.fahim.shieldcheck.worker

import android.content.Context
import androidx.hilt.work.HiltWorker
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import com.fahim.shieldcheck.domain.usecase.app.ScanAppsUseCase
import com.fahim.shieldcheck.domain.usecase.device.CheckDeviceSecurityUseCase
import com.fahim.shieldcheck.domain.usecase.network.ScanNetworkSecurityUseCase
import dagger.assisted.Assisted
import dagger.assisted.AssistedInject
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

@HiltWorker
class PeriodicScanWorker @AssistedInject constructor(
    @Assisted context: Context,
    @Assisted workerParams: WorkerParameters,
    private val scanAppsUseCase: ScanAppsUseCase,
    private val checkDeviceSecurityUseCase: CheckDeviceSecurityUseCase,
    private val scanNetworkSecurityUseCase: ScanNetworkSecurityUseCase
) : CoroutineWorker(context, workerParams) {

    override suspend fun doWork(): Result {
        return withContext(Dispatchers.IO) {
            try {
                // Run all security scans
                scanAppsUseCase()
                checkDeviceSecurityUseCase()
                scanNetworkSecurityUseCase()

                Result.success()
            } catch (e: Exception) {
                if (runAttemptCount < MAX_RETRY_COUNT) {
                    Result.retry()
                } else {
                    Result.failure()
                }
            }
        }
    }

    companion object {
        const val WORK_NAME = "periodic_security_scan"
        private const val MAX_RETRY_COUNT = 3
    }
}
