package com.fahim.shieldcheck.presentation.screens.devicesecurity

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.fahim.shieldcheck.domain.model.device.DeviceSecurityStatus
import com.fahim.shieldcheck.domain.usecase.device.CheckDeviceSecurityUseCase
import com.fahim.shieldcheck.domain.usecase.device.GetLatestDeviceSecurityUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

data class DeviceSecurityUiState(
    val isLoading: Boolean = true,
    val isScanning: Boolean = false,
    val securityStatus: DeviceSecurityStatus? = null,
    val error: String? = null
)

@HiltViewModel
class DeviceSecurityViewModel @Inject constructor(
    private val checkDeviceSecurityUseCase: CheckDeviceSecurityUseCase,
    private val getLatestDeviceSecurityUseCase: GetLatestDeviceSecurityUseCase
) : ViewModel() {

    private val _uiState = MutableStateFlow(DeviceSecurityUiState())
    val uiState: StateFlow<DeviceSecurityUiState> = _uiState.asStateFlow()

    init {
        loadCachedResult()
        scanDeviceSecurity()
    }

    private fun loadCachedResult() {
        viewModelScope.launch {
            getLatestDeviceSecurityUseCase()
                .catch { /* Ignore cache errors */ }
                .collect { status ->
                    if (status != null && _uiState.value.securityStatus == null) {
                        _uiState.update { it.copy(securityStatus = status, isLoading = false) }
                    }
                }
        }
    }

    fun scanDeviceSecurity() {
        viewModelScope.launch {
            _uiState.update { it.copy(isScanning = true, error = null) }

            try {
                val status = checkDeviceSecurityUseCase()
                _uiState.update {
                    it.copy(
                        isLoading = false,
                        isScanning = false,
                        securityStatus = status
                    )
                }
            } catch (e: Exception) {
                _uiState.update {
                    it.copy(
                        isLoading = false,
                        isScanning = false,
                        error = e.message ?: "Failed to scan device security"
                    )
                }
            }
        }
    }

    fun clearError() {
        _uiState.update { it.copy(error = null) }
    }
}
