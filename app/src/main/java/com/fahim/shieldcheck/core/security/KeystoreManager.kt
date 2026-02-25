package com.fahim.shieldcheck.core.security

import android.content.Context
import android.content.SharedPreferences
import androidx.security.crypto.EncryptedSharedPreferences
import androidx.security.crypto.MasterKey
import dagger.hilt.android.qualifiers.ApplicationContext
import java.security.SecureRandom
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class KeystoreManager @Inject constructor(
    @ApplicationContext private val context: Context
) {

    private val masterKey: MasterKey by lazy {
        MasterKey.Builder(context)
            .setKeyScheme(MasterKey.KeyScheme.AES256_GCM)
            .build()
    }

    private val encryptedPrefs: SharedPreferences by lazy {
        EncryptedSharedPreferences.create(
            context,
            KEYSTORE_PREFS_FILE,
            masterKey,
            EncryptedSharedPreferences.PrefKeyEncryptionScheme.AES256_SIV,
            EncryptedSharedPreferences.PrefValueEncryptionScheme.AES256_GCM
        )
    }

    fun getDatabasePassphrase(): ByteArray {
        val stored = encryptedPrefs.getString(KEY_DB_PASSPHRASE, null)
        if (stored != null) {
            return stored.toByteArray(Charsets.UTF_8)
        }

        // Generate a stable random passphrase on first launch
        val passphrase = generateRandomPassphrase()
        encryptedPrefs.edit().putString(KEY_DB_PASSPHRASE, passphrase).apply()
        return passphrase.toByteArray(Charsets.UTF_8)
    }

    private fun generateRandomPassphrase(): String {
        val bytes = ByteArray(32)
        SecureRandom().nextBytes(bytes)
        return bytes.joinToString("") { "%02x".format(it) }
    }

    companion object {
        private const val KEYSTORE_PREFS_FILE = "shieldcheck_keystore_prefs"
        private const val KEY_DB_PASSPHRASE = "db_passphrase"
    }
}
