package com.fahim.shieldcheck.presentation.screens.devicesecurity

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.fahim.shieldcheck.domain.model.device.DeviceSecurityStatus
import com.fahim.shieldcheck.domain.model.device.IssueSeverity
import com.fahim.shieldcheck.domain.model.device.SecurityIssue
import com.fahim.shieldcheck.presentation.common.components.ExpandableCard
import com.fahim.shieldcheck.presentation.common.components.LoadingIndicator
import com.fahim.shieldcheck.presentation.common.components.SecurityScoreCard

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DeviceSecurityScreen(
    onNavigateBack: () -> Unit,
    viewModel: DeviceSecurityViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    val snackbarHostState = remember { SnackbarHostState() }

    LaunchedEffect(uiState.error) {
        uiState.error?.let {
            snackbarHostState.showSnackbar(it)
            viewModel.clearError()
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Device Security") },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back")
                    }
                },
                actions = {
                    IconButton(
                        onClick = { viewModel.scanDeviceSecurity() },
                        enabled = !uiState.isScanning
                    ) {
                        Icon(Icons.Default.Refresh, contentDescription = "Rescan")
                    }
                }
            )
        },
        snackbarHost = { SnackbarHost(snackbarHostState) }
    ) { paddingValues ->
        if (uiState.isLoading && uiState.securityStatus == null) {
            LoadingIndicator(message = "Checking device security...")
        } else {
            uiState.securityStatus?.let { status ->
                DeviceSecurityContent(
                    status = status,
                    modifier = Modifier.padding(paddingValues)
                )
            }
        }
    }
}

@Composable
private fun DeviceSecurityContent(
    status: DeviceSecurityStatus,
    modifier: Modifier = Modifier
) {
    LazyColumn(
        modifier = modifier.fillMaxSize(),
        contentPadding = PaddingValues(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // Security Score Card
        item {
            SecurityScoreCard(
                score = status.overallScore,
                title = "Device Security Score",
                subtitle = "Based on ${6 - status.securityIssues.size} of 6 checks passed"
            )
        }

        // Security Issues
        if (status.hasSecurityIssues) {
            item {
                Text(
                    text = "Security Issues Found",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.error
                )
            }

            items(status.securityIssues) { issue ->
                SecurityIssueCard(issue = issue)
            }
        }

        // Security Checks
        item {
            ExpandableCard(
                title = "Security Checks",
                subtitle = "Detailed security status",
                initialExpanded = true
            ) {
                Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                    SecurityCheckItem(
                        title = "Root Detection",
                        isPassing = !status.isRooted,
                        description = if (status.isRooted) "Device is rooted" else "Device is not rooted"
                    )
                    SecurityCheckItem(
                        title = "Storage Encryption",
                        isPassing = status.isEncrypted,
                        description = if (status.isEncrypted) "Storage is encrypted" else "Storage is not encrypted"
                    )
                    SecurityCheckItem(
                        title = "Screen Lock",
                        isPassing = status.hasScreenLock,
                        description = if (status.hasScreenLock) "Screen lock is enabled" else "No screen lock set"
                    )
                    SecurityCheckItem(
                        title = "Developer Options",
                        isPassing = !status.isDeveloperOptionsEnabled,
                        description = if (status.isDeveloperOptionsEnabled) "Developer options enabled" else "Developer options disabled"
                    )
                    SecurityCheckItem(
                        title = "USB Debugging",
                        isPassing = !status.isUsbDebuggingEnabled,
                        description = if (status.isUsbDebuggingEnabled) "USB debugging enabled" else "USB debugging disabled"
                    )
                    SecurityCheckItem(
                        title = "Unknown Sources",
                        isPassing = !status.isUnknownSourcesEnabled,
                        description = if (status.isUnknownSourcesEnabled) "Unknown sources enabled" else "Unknown sources disabled"
                    )
                }
            }
        }

        // Device Information
        item {
            ExpandableCard(
                title = "Device Information",
                subtitle = "${status.manufacturer} ${status.deviceModel}"
            ) {
                Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    DeviceInfoRow("Manufacturer", status.manufacturer)
                    DeviceInfoRow("Model", status.deviceModel)
                    DeviceInfoRow("Android Version", status.androidVersion)
                    DeviceInfoRow("SDK Version", status.sdkVersion.toString())
                    status.securityPatchLevel?.let {
                        DeviceInfoRow("Security Patch", it)
                    }
                }
            }
        }
    }
}

@Composable
private fun SecurityIssueCard(issue: SecurityIssue) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(
            containerColor = when (issue.severity) {
                IssueSeverity.CRITICAL -> MaterialTheme.colorScheme.errorContainer
                IssueSeverity.WARNING -> MaterialTheme.colorScheme.tertiaryContainer
                IssueSeverity.INFO -> MaterialTheme.colorScheme.secondaryContainer
            }
        )
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalAlignment = Alignment.Top
        ) {
            Icon(
                imageVector = when (issue.severity) {
                    IssueSeverity.CRITICAL -> Icons.Default.Close
                    IssueSeverity.WARNING -> Icons.Default.Warning
                    IssueSeverity.INFO -> Icons.Default.Info
                },
                contentDescription = null,
                tint = when (issue.severity) {
                    IssueSeverity.CRITICAL -> MaterialTheme.colorScheme.error
                    IssueSeverity.WARNING -> Color(0xFFF57C00)
                    IssueSeverity.INFO -> MaterialTheme.colorScheme.secondary
                }
            )

            Column(
                modifier = Modifier
                    .weight(1f)
                    .padding(start = 12.dp)
            ) {
                Text(
                    text = issue.title,
                    style = MaterialTheme.typography.titleSmall,
                    fontWeight = FontWeight.Bold
                )
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = issue.description,
                    style = MaterialTheme.typography.bodySmall
                )
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    text = "Recommendation: ${issue.recommendation}",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }
    }
}

@Composable
private fun SecurityCheckItem(
    title: String,
    isPassing: Boolean,
    description: String
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = title,
                style = MaterialTheme.typography.bodyMedium,
                fontWeight = FontWeight.Medium
            )
            Text(
                text = description,
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }

        Icon(
            imageVector = if (isPassing) Icons.Default.Check else Icons.Default.Close,
            contentDescription = if (isPassing) "Passing" else "Failing",
            tint = if (isPassing) Color(0xFF4CAF50) else MaterialTheme.colorScheme.error
        )
    }
}

@Composable
private fun DeviceInfoRow(label: String, value: String) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Text(
            text = label,
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
        Text(
            text = value,
            style = MaterialTheme.typography.bodyMedium
        )
    }
}
