package com.fahim.shieldcheck.presentation.screens.appaudit

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.fahim.shieldcheck.domain.model.app.InstalledApp
import com.fahim.shieldcheck.domain.usecase.app.GetAppByPackageUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

data class AppDetailUiState(
    val isLoading: Boolean = true,
    val app: InstalledApp? = null,
    val error: String? = null
)

@HiltViewModel
class AppDetailViewModel @Inject constructor(
    private val getAppByPackageUseCase: GetAppByPackageUseCase,
    savedStateHandle: SavedStateHandle
) : ViewModel() {

    private val packageName: String = savedStateHandle.get<String>("packageName") ?: ""

    private val _uiState = MutableStateFlow(AppDetailUiState())
    val uiState: StateFlow<AppDetailUiState> = _uiState.asStateFlow()

    init {
        loadAppDetails()
    }

    fun loadAppDetails() {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true, error = null) }

            try {
                val app = getAppByPackageUseCase(packageName)
                if (app != null) {
                    _uiState.update { it.copy(isLoading = false, app = app) }
                } else {
                    _uiState.update { it.copy(isLoading = false, error = "App not found") }
                }
            } catch (e: Exception) {
                _uiState.update {
                    it.copy(isLoading = false, error = e.message ?: "Failed to load app details")
                }
            }
        }
    }

    fun clearError() {
        _uiState.update { it.copy(error = null) }
    }
}
