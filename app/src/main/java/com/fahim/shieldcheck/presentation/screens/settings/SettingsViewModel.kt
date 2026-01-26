package com.fahim.shieldcheck.presentation.screens.settings

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.fahim.shieldcheck.core.security.EncryptedPreferencesManager
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

data class SettingsUiState(
    val autoScanEnabled: Boolean = false,
    val notificationsEnabled: Boolean = true,
    val scanIntervalHours: Int = 24
)

@HiltViewModel
class SettingsViewModel @Inject constructor(
    private val preferencesManager: EncryptedPreferencesManager
) : ViewModel() {

    private val _uiState = MutableStateFlow(SettingsUiState())
    val uiState: StateFlow<SettingsUiState> = _uiState.asStateFlow()

    init {
        loadSettings()
    }

    private fun loadSettings() {
        viewModelScope.launch {
            _uiState.update {
                SettingsUiState(
                    autoScanEnabled = preferencesManager.getBoolean(
                        EncryptedPreferencesManager.KEY_AUTO_SCAN_ENABLED,
                        false
                    ),
                    notificationsEnabled = preferencesManager.getBoolean(
                        EncryptedPreferencesManager.KEY_NOTIFICATIONS_ENABLED,
                        true
                    ),
                    scanIntervalHours = preferencesManager.getInt(
                        EncryptedPreferencesManager.KEY_SCAN_INTERVAL_HOURS,
                        24
                    )
                )
            }
        }
    }

    fun setAutoScanEnabled(enabled: Boolean) {
        preferencesManager.putBoolean(EncryptedPreferencesManager.KEY_AUTO_SCAN_ENABLED, enabled)
        _uiState.update { it.copy(autoScanEnabled = enabled) }
    }

    fun setNotificationsEnabled(enabled: Boolean) {
        preferencesManager.putBoolean(EncryptedPreferencesManager.KEY_NOTIFICATIONS_ENABLED, enabled)
        _uiState.update { it.copy(notificationsEnabled = enabled) }
    }

    fun setScanInterval(hours: Int) {
        preferencesManager.putInt(EncryptedPreferencesManager.KEY_SCAN_INTERVAL_HOURS, hours)
        _uiState.update { it.copy(scanIntervalHours = hours) }
    }
}
