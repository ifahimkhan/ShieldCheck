package com.fahim.shieldcheck.presentation.screens.network

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.fahim.shieldcheck.domain.model.network.NetworkSecurityStatus
import com.fahim.shieldcheck.domain.usecase.network.GetLatestNetworkScanUseCase
import com.fahim.shieldcheck.domain.usecase.network.ScanNetworkSecurityUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

data class NetworkScanUiState(
    val isLoading: Boolean = true,
    val isScanning: Boolean = false,
    val networkStatus: NetworkSecurityStatus? = null,
    val error: String? = null
)

@HiltViewModel
class NetworkScanViewModel @Inject constructor(
    private val scanNetworkSecurityUseCase: ScanNetworkSecurityUseCase,
    private val getLatestNetworkScanUseCase: GetLatestNetworkScanUseCase
) : ViewModel() {

    private val _uiState = MutableStateFlow(NetworkScanUiState())
    val uiState: StateFlow<NetworkScanUiState> = _uiState.asStateFlow()

    init {
        loadCachedResult()
        scanNetwork()
    }

    private fun loadCachedResult() {
        viewModelScope.launch {
            getLatestNetworkScanUseCase()
                .catch { /* Ignore cache errors */ }
                .collect { status ->
                    if (status != null && _uiState.value.networkStatus == null) {
                        _uiState.update { it.copy(networkStatus = status, isLoading = false) }
                    }
                }
        }
    }

    fun scanNetwork() {
        viewModelScope.launch {
            _uiState.update { it.copy(isScanning = true, error = null) }

            try {
                val status = scanNetworkSecurityUseCase()
                _uiState.update {
                    it.copy(
                        isLoading = false,
                        isScanning = false,
                        networkStatus = status
                    )
                }
            } catch (e: Exception) {
                _uiState.update {
                    it.copy(
                        isLoading = false,
                        isScanning = false,
                        error = e.message ?: "Failed to scan network"
                    )
                }
            }
        }
    }

    fun clearError() {
        _uiState.update { it.copy(error = null) }
    }
}
