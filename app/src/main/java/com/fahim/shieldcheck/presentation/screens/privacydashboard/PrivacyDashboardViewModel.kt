package com.fahim.shieldcheck.presentation.screens.privacydashboard

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.fahim.shieldcheck.domain.model.dashboard.PrivacySummary
import com.fahim.shieldcheck.domain.usecase.dashboard.GetPrivacySummaryUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

data class PrivacyDashboardUiState(
    val isLoading: Boolean = true,
    val summary: PrivacySummary? = null,
    val error: String? = null
)

@HiltViewModel
class PrivacyDashboardViewModel @Inject constructor(
    private val getPrivacySummaryUseCase: GetPrivacySummaryUseCase
) : ViewModel() {

    private val _uiState = MutableStateFlow(PrivacyDashboardUiState())
    val uiState: StateFlow<PrivacyDashboardUiState> = _uiState.asStateFlow()

    init {
        loadSummary()
    }

    fun loadSummary() {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true, error = null) }

            try {
                val summary = getPrivacySummaryUseCase()
                _uiState.update {
                    it.copy(isLoading = false, summary = summary)
                }
            } catch (e: Exception) {
                _uiState.update {
                    it.copy(isLoading = false, error = e.message ?: "Failed to load summary")
                }
            }
        }
    }

    fun clearError() {
        _uiState.update { it.copy(error = null) }
    }
}
