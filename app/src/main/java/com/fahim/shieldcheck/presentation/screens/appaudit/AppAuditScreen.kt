package com.fahim.shieldcheck.presentation.screens.appaudit

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.FilterList
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilterChip
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.fahim.shieldcheck.domain.model.app.InstalledApp
import com.fahim.shieldcheck.domain.model.app.RiskLevel
import com.fahim.shieldcheck.presentation.common.components.LoadingIndicator
import com.fahim.shieldcheck.presentation.screens.appaudit.components.AppListItem
import com.fahim.shieldcheck.presentation.screens.appaudit.components.AppStatisticsCard
import com.fahim.shieldcheck.ui.theme.ShieldCheckTheme

@Composable
fun AppAuditScreen(
    onNavigateBack: () -> Unit,
    onNavigateToAppDetail: (String) -> Unit,
    viewModel: AppAuditViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

    AppAuditScreen(
        uiState = uiState,
        onNavigateBack = onNavigateBack,
        onNavigateToAppDetail = onNavigateToAppDetail,
        onScanApps = viewModel::scanApps,
        onSearchQueryChanged = viewModel::setSearchQuery,
        onFilterSelected = viewModel::setFilter,
        onToggleSystemApps = viewModel::toggleSystemApps,
        onClearError = viewModel::clearError
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun AppAuditScreen(
    uiState: AppAuditUiState,
    onNavigateBack: () -> Unit,
    onNavigateToAppDetail: (String) -> Unit,
    onScanApps: () -> Unit,
    onSearchQueryChanged: (String) -> Unit,
    onFilterSelected: (AppFilter) -> Unit,
    onToggleSystemApps: () -> Unit,
    onClearError: () -> Unit
) {
    val snackbarHostState = remember { SnackbarHostState() }
    val scrollBehavior = TopAppBarDefaults.pinnedScrollBehavior()

    LaunchedEffect(uiState.error) {
        uiState.error?.let {
            snackbarHostState.showSnackbar(it)
            onClearError()
        }
    }

    Scaffold(
        modifier = Modifier.nestedScroll(scrollBehavior.nestedScrollConnection),
        topBar = {
            TopAppBar(
                title = { Text("App Permission Audit") },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back")
                    }
                },
                actions = {
                    IconButton(
                        onClick = onScanApps,
                        enabled = !uiState.isScanning
                    ) {
                        Icon(Icons.Default.Refresh, contentDescription = "Refresh")
                    }
                },
                scrollBehavior = scrollBehavior
            )
        },
        snackbarHost = { SnackbarHost(snackbarHostState) }
    ) { paddingValues ->
        if (uiState.isLoading) {
            LoadingIndicator(message = "Loading apps...")
        } else {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues)
            ) {
                // Statistics Card
                uiState.statistics?.let { stats ->
                    AppStatisticsCard(
                        statistics = stats,
                        modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp)
                    )
                }

                // Search Bar
                OutlinedTextField(
                    value = uiState.searchQuery,
                    onValueChange = onSearchQueryChanged,
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp),
                    placeholder = { Text("Search apps...") },
                    leadingIcon = { Icon(Icons.Default.Search, contentDescription = null) },
                    singleLine = true
                )

                Spacer(modifier = Modifier.height(8.dp))

                // Filter Chips
                LazyRow(
                    contentPadding = PaddingValues(horizontal = 16.dp),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    items(AppFilter.entries) { filter ->
                        FilterChip(
                            selected = uiState.selectedFilter == filter,
                            onClick = { onFilterSelected(filter) },
                            label = { Text(filter.name) }
                        )
                    }
                }

                // System Apps Toggle
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp, vertical = 8.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "Show System Apps",
                        style = MaterialTheme.typography.bodyMedium
                    )
                    Switch(
                        checked = uiState.showSystemApps,
                        onCheckedChange = { onToggleSystemApps() }
                    )
                }

                // App Count
                Text(
                    text = "${uiState.filteredApps.size} apps",
                    style = MaterialTheme.typography.labelMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    modifier = Modifier.padding(horizontal = 16.dp, vertical = 4.dp)
                )

                // App List
                if (uiState.filteredApps.isEmpty()) {
                    Box(
                        modifier = Modifier.fillMaxSize(),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = "No apps found",
                            style = MaterialTheme.typography.bodyLarge,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                } else {
                    LazyColumn(
                        contentPadding = PaddingValues(16.dp),
                        verticalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        items(
                            items = uiState.filteredApps,
                            key = { it.packageName }
                        ) { app ->
                            AppListItem(
                                app = app,
                                onClick = { onNavigateToAppDetail(app.packageName) }
                            )
                        }
                    }
                }
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
private fun AppAuditScreenPreview() {
    val mockApps = listOf(
        InstalledApp(
            packageName = "com.example.social",
            appName = "Social Media",
            versionName = "2.1.0",
            versionCode = 21,
            isSystemApp = false,
            installedDate = null,
            lastUpdatedDate = null,
            icon = null,
            permissions = emptyList(),
            riskScore = 65,
            riskLevel = RiskLevel.HIGH
        ),
        InstalledApp(
            packageName = "com.example.camera",
            appName = "Camera App",
            versionName = "1.0.0",
            versionCode = 1,
            isSystemApp = false,
            installedDate = null,
            lastUpdatedDate = null,
            icon = null,
            permissions = emptyList(),
            riskScore = 30,
            riskLevel = RiskLevel.MEDIUM
        )
    )

    ShieldCheckTheme {
        AppAuditScreen(
            uiState = AppAuditUiState(
                isLoading = false,
                filteredApps = mockApps
            ),
            onNavigateBack = {},
            onNavigateToAppDetail = {},
            onScanApps = {},
            onSearchQueryChanged = {},
            onFilterSelected = {},
            onToggleSystemApps = {},
            onClearError = {}
        )
    }
}
