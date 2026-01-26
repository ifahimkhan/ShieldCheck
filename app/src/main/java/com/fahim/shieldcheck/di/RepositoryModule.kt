package com.fahim.shieldcheck.di

import com.fahim.shieldcheck.data.repository.AppRepositoryImpl
import com.fahim.shieldcheck.data.repository.DeviceSecurityRepositoryImpl
import com.fahim.shieldcheck.data.repository.NetworkRepositoryImpl
import com.fahim.shieldcheck.data.repository.PrivacyDashboardRepositoryImpl
import com.fahim.shieldcheck.domain.repository.AppRepository
import com.fahim.shieldcheck.domain.repository.DeviceSecurityRepository
import com.fahim.shieldcheck.domain.repository.NetworkRepository
import com.fahim.shieldcheck.domain.repository.PrivacyDashboardRepository
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
abstract class RepositoryModule {

    @Binds
    @Singleton
    abstract fun bindAppRepository(impl: AppRepositoryImpl): AppRepository

    @Binds
    @Singleton
    abstract fun bindDeviceSecurityRepository(impl: DeviceSecurityRepositoryImpl): DeviceSecurityRepository

    @Binds
    @Singleton
    abstract fun bindNetworkRepository(impl: NetworkRepositoryImpl): NetworkRepository

    @Binds
    @Singleton
    abstract fun bindPrivacyDashboardRepository(impl: PrivacyDashboardRepositoryImpl): PrivacyDashboardRepository
}
