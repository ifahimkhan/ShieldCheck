package com.fahim.shieldcheck.core.security

import android.security.keystore.KeyGenParameterSpec
import android.security.keystore.KeyProperties
import java.security.KeyStore
import javax.crypto.KeyGenerator
import javax.crypto.SecretKey
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class KeystoreManager @Inject constructor() {

    private val keyStore: KeyStore = KeyStore.getInstance(ANDROID_KEYSTORE).apply {
        load(null)
    }

    fun getOrCreateDatabaseKey(): SecretKey {
        return if (keyStore.containsAlias(DATABASE_KEY_ALIAS)) {
            keyStore.getKey(DATABASE_KEY_ALIAS, null) as SecretKey
        } else {
            generateDatabaseKey()
        }
    }

    private fun generateDatabaseKey(): SecretKey {
        val keyGenerator = KeyGenerator.getInstance(
            KeyProperties.KEY_ALGORITHM_AES,
            ANDROID_KEYSTORE
        )

        val keySpec = KeyGenParameterSpec.Builder(
            DATABASE_KEY_ALIAS,
            KeyProperties.PURPOSE_ENCRYPT or KeyProperties.PURPOSE_DECRYPT
        )
            .setBlockModes(KeyProperties.BLOCK_MODE_GCM)
            .setEncryptionPaddings(KeyProperties.ENCRYPTION_PADDING_NONE)
            .setKeySize(256)
            .setUserAuthenticationRequired(false)
            .build()

        keyGenerator.init(keySpec)
        return keyGenerator.generateKey()
    }

    fun getDatabasePassphrase(): ByteArray {
        val secretKey = getOrCreateDatabaseKey()
        return secretKey.encoded ?: generateFallbackPassphrase()
    }

    private fun generateFallbackPassphrase(): ByteArray {
        // Fallback for devices where encoded key is null
        return "shieldcheck_secure_db_${System.currentTimeMillis()}".toByteArray()
    }

    fun keyExists(alias: String): Boolean {
        return keyStore.containsAlias(alias)
    }

    fun deleteKey(alias: String) {
        if (keyStore.containsAlias(alias)) {
            keyStore.deleteEntry(alias)
        }
    }

    companion object {
        private const val ANDROID_KEYSTORE = "AndroidKeyStore"
        private const val DATABASE_KEY_ALIAS = "shieldcheck_db_key"
    }
}
