package com.fahim.shieldcheck.di

import com.fahim.shieldcheck.core.security.EncryptedPreferencesManager
import com.fahim.shieldcheck.core.security.KeystoreManager
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object SecurityModule {

    @Provides
    @Singleton
    fun provideKeystoreManager(): KeystoreManager {
        return KeystoreManager()
    }
}
