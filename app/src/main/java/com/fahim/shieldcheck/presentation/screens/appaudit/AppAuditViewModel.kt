package com.fahim.shieldcheck.presentation.screens.appaudit

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.fahim.shieldcheck.domain.model.app.InstalledApp
import com.fahim.shieldcheck.domain.model.app.RiskLevel
import com.fahim.shieldcheck.domain.usecase.app.GetAppStatisticsUseCase
import com.fahim.shieldcheck.domain.usecase.app.GetInstalledAppsUseCase
import com.fahim.shieldcheck.domain.usecase.app.AppStatistics
import com.fahim.shieldcheck.domain.usecase.app.ScanAppsUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

data class AppAuditUiState(
    val isLoading: Boolean = true,
    val isScanning: Boolean = false,
    val apps: List<InstalledApp> = emptyList(),
    val filteredApps: List<InstalledApp> = emptyList(),
    val statistics: AppStatistics? = null,
    val selectedFilter: AppFilter = AppFilter.ALL,
    val searchQuery: String = "",
    val showSystemApps: Boolean = false,
    val error: String? = null
)

enum class AppFilter {
    ALL, CRITICAL, HIGH, MEDIUM, LOW, SAFE
}

@HiltViewModel
class AppAuditViewModel @Inject constructor(
    private val getInstalledAppsUseCase: GetInstalledAppsUseCase,
    private val scanAppsUseCase: ScanAppsUseCase,
    private val getAppStatisticsUseCase: GetAppStatisticsUseCase
) : ViewModel() {

    private val _uiState = MutableStateFlow(AppAuditUiState())
    val uiState: StateFlow<AppAuditUiState> = _uiState.asStateFlow()

    init {
        loadApps()
    }

    fun loadApps() {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true, error = null) }

            getInstalledAppsUseCase()
                .catch { e ->
                    _uiState.update {
                        it.copy(isLoading = false, error = e.message ?: "Failed to load apps")
                    }
                }
                .collect { apps ->
                    _uiState.update { state ->
                        state.copy(
                            isLoading = false,
                            apps = apps,
                            filteredApps = applyFilters(apps, state.selectedFilter, state.searchQuery, state.showSystemApps)
                        )
                    }
                    loadStatistics()
                }
        }
    }

    fun scanApps() {
        viewModelScope.launch {
            _uiState.update { it.copy(isScanning = true, error = null) }

            try {
                val apps = scanAppsUseCase()
                _uiState.update { state ->
                    state.copy(
                        isScanning = false,
                        apps = apps,
                        filteredApps = applyFilters(apps, state.selectedFilter, state.searchQuery, state.showSystemApps)
                    )
                }
                loadStatistics()
            } catch (e: Exception) {
                _uiState.update {
                    it.copy(isScanning = false, error = e.message ?: "Scan failed")
                }
            }
        }
    }

    private suspend fun loadStatistics() {
        try {
            val statistics = getAppStatisticsUseCase()
            _uiState.update { it.copy(statistics = statistics) }
        } catch (e: Exception) {
            // Statistics loading failure is non-critical
        }
    }

    fun setFilter(filter: AppFilter) {
        _uiState.update { state ->
            state.copy(
                selectedFilter = filter,
                filteredApps = applyFilters(state.apps, filter, state.searchQuery, state.showSystemApps)
            )
        }
    }

    fun setSearchQuery(query: String) {
        _uiState.update { state ->
            state.copy(
                searchQuery = query,
                filteredApps = applyFilters(state.apps, state.selectedFilter, query, state.showSystemApps)
            )
        }
    }

    fun toggleSystemApps() {
        _uiState.update { state ->
            val newShowSystemApps = !state.showSystemApps
            state.copy(
                showSystemApps = newShowSystemApps,
                filteredApps = applyFilters(state.apps, state.selectedFilter, state.searchQuery, newShowSystemApps)
            )
        }
    }

    fun clearError() {
        _uiState.update { it.copy(error = null) }
    }

    private fun applyFilters(
        apps: List<InstalledApp>,
        filter: AppFilter,
        query: String,
        showSystemApps: Boolean
    ): List<InstalledApp> {
        return apps
            .filter { app ->
                if (!showSystemApps && app.isSystemApp) false
                else true
            }
            .filter { app ->
                when (filter) {
                    AppFilter.ALL -> true
                    AppFilter.CRITICAL -> app.riskLevel == RiskLevel.CRITICAL
                    AppFilter.HIGH -> app.riskLevel == RiskLevel.HIGH
                    AppFilter.MEDIUM -> app.riskLevel == RiskLevel.MEDIUM
                    AppFilter.LOW -> app.riskLevel == RiskLevel.LOW
                    AppFilter.SAFE -> app.riskLevel == RiskLevel.SAFE
                }
            }
            .filter { app ->
                if (query.isBlank()) true
                else app.appName.contains(query, ignoreCase = true) ||
                        app.packageName.contains(query, ignoreCase = true)
            }
    }
}
