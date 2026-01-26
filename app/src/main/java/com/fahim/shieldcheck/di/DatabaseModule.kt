package com.fahim.shieldcheck.di

import android.content.Context
import com.fahim.shieldcheck.core.security.KeystoreManager
import com.fahim.shieldcheck.data.local.db.ShieldCheckDatabase
import com.fahim.shieldcheck.data.local.db.dao.AppScanResultDao
import com.fahim.shieldcheck.data.local.db.dao.DeviceScanResultDao
import com.fahim.shieldcheck.data.local.db.dao.NetworkScanResultDao
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object DatabaseModule {

    @Provides
    @Singleton
    fun provideDatabase(
        @ApplicationContext context: Context,
        keystoreManager: KeystoreManager
    ): ShieldCheckDatabase {
        val passphrase = keystoreManager.getDatabasePassphrase()
        return ShieldCheckDatabase.getInstance(context, passphrase)
    }

    @Provides
    @Singleton
    fun provideAppScanResultDao(database: ShieldCheckDatabase): AppScanResultDao {
        return database.appScanResultDao()
    }

    @Provides
    @Singleton
    fun provideDeviceScanResultDao(database: ShieldCheckDatabase): DeviceScanResultDao {
        return database.deviceScanResultDao()
    }

    @Provides
    @Singleton
    fun provideNetworkScanResultDao(database: ShieldCheckDatabase): NetworkScanResultDao {
        return database.networkScanResultDao()
    }
}
