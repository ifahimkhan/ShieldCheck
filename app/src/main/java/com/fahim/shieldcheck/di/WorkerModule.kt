package com.fahim.shieldcheck.di

import com.fahim.shieldcheck.worker.ScanScheduler
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object WorkerModule {

    // ScanScheduler is provided via @Inject constructor
    // This module is here for any additional worker-related dependencies
}
