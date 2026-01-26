package com.fahim.shieldcheck.worker

import android.content.Context
import androidx.work.Constraints
import androidx.work.ExistingPeriodicWorkPolicy
import androidx.work.NetworkType
import androidx.work.PeriodicWorkRequestBuilder
import androidx.work.WorkManager
import dagger.hilt.android.qualifiers.ApplicationContext
import java.util.concurrent.TimeUnit
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class ScanScheduler @Inject constructor(
    @ApplicationContext private val context: Context
) {

    private val workManager = WorkManager.getInstance(context)

    fun schedulePeriodicScan(intervalHours: Long = 24) {
        val constraints = Constraints.Builder()
            .setRequiredNetworkType(NetworkType.NOT_REQUIRED)
            .setRequiresBatteryNotLow(true)
            .build()

        val periodicWorkRequest = PeriodicWorkRequestBuilder<PeriodicScanWorker>(
            intervalHours, TimeUnit.HOURS
        )
            .setConstraints(constraints)
            .setInitialDelay(1, TimeUnit.HOURS) // Start first scan after 1 hour
            .build()

        workManager.enqueueUniquePeriodicWork(
            PeriodicScanWorker.WORK_NAME,
            ExistingPeriodicWorkPolicy.UPDATE,
            periodicWorkRequest
        )
    }

    fun cancelPeriodicScan() {
        workManager.cancelUniqueWork(PeriodicScanWorker.WORK_NAME)
    }

    fun isPeriodicScanScheduled(): Boolean {
        val workInfos = workManager.getWorkInfosForUniqueWork(PeriodicScanWorker.WORK_NAME).get()
        return workInfos.isNotEmpty() && !workInfos.all { it.state.isFinished }
    }
}
