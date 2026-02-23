package com.fahim.shieldcheck.presentation.screens.network

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
import androidx.compose.material.icons.filled.Shield
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material.icons.filled.Wifi
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
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.fahim.shieldcheck.domain.model.network.NetworkIssue
import com.fahim.shieldcheck.domain.model.network.NetworkIssueSeverity
import com.fahim.shieldcheck.domain.model.network.NetworkSecurityStatus
import com.fahim.shieldcheck.domain.model.network.OpenPort
import com.fahim.shieldcheck.domain.model.network.WifiSecurityInfo
import com.fahim.shieldcheck.domain.model.network.WifiSecurityType
import com.fahim.shieldcheck.presentation.common.components.ExpandableCard
import com.fahim.shieldcheck.presentation.common.components.LoadingIndicator
import com.fahim.shieldcheck.presentation.common.components.SecurityScoreCard
import com.fahim.shieldcheck.ui.theme.ShieldCheckTheme

@Composable
fun NetworkScanScreen(
    onNavigateBack: () -> Unit,
    viewModel: NetworkScanViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

    NetworkScanScreen(
        uiState = uiState,
        onNavigateBack = onNavigateBack,
        onRescan = viewModel::scanNetwork,
        onClearError = viewModel::clearError
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun NetworkScanScreen(
    uiState: NetworkScanUiState,
    onNavigateBack: () -> Unit,
    onRescan: () -> Unit,
    onClearError: () -> Unit
) {
    val snackbarHostState = remember { SnackbarHostState() }

    LaunchedEffect(uiState.error) {
        uiState.error?.let {
            snackbarHostState.showSnackbar(it)
            onClearError()
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Network Security") },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back")
                    }
                },
                actions = {
                    IconButton(
                        onClick = onRescan,
                        enabled = !uiState.isScanning
                    ) {
                        Icon(Icons.Default.Refresh, contentDescription = "Rescan")
                    }
                }
            )
        },
        snackbarHost = { SnackbarHost(snackbarHostState) }
    ) { paddingValues ->
        if (uiState.isLoading && uiState.networkStatus == null) {
            LoadingIndicator(message = "Scanning network...")
        } else {
            uiState.networkStatus?.let { status ->
                NetworkScanContent(
                    status = status,
                    modifier = Modifier.padding(paddingValues)
                )
            }
        }
    }
}

@Composable
private fun NetworkScanContent(
    status: NetworkSecurityStatus,
    modifier: Modifier = Modifier
) {
    LazyColumn(
        modifier = modifier.fillMaxSize(),
        contentPadding = PaddingValues(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // Security Score
        item {
            SecurityScoreCard(
                score = status.overallScore,
                title = "Network Security Score",
                subtitle = if (status.isVpnActive) "VPN Active" else null
            )
        }

        // Network Issues
        if (status.hasIssues) {
            item {
                Text(
                    text = "Security Issues",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold
                )
            }

            items(status.issues) { issue ->
                NetworkIssueCard(issue = issue)
            }
        }

        // WiFi Information
        item {
            status.wifiInfo?.let { wifi ->
                WifiInfoCard(wifiInfo = wifi)
            } ?: run {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    colors = CardDefaults.cardColors(
                        containerColor = MaterialTheme.colorScheme.surfaceVariant
                    )
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(16.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.Wifi,
                            contentDescription = null,
                            tint = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                        Text(
                            text = "Not connected to WiFi",
                            style = MaterialTheme.typography.bodyMedium
                        )
                    }
                }
            }
        }

        // VPN Status
        item {
            VpnStatusCard(isActive = status.isVpnActive)
        }

        // Open Ports
        if (status.openPorts.isNotEmpty()) {
            item {
                ExpandableCard(
                    title = "Open Ports",
                    subtitle = "${status.openPorts.size} ports detected"
                ) {
                    Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                        status.openPorts.forEach { port ->
                            OpenPortItem(port = port)
                        }
                    }
                }
            }
        }

        // Network Details
        item {
            ExpandableCard(
                title = "Network Details"
            ) {
                Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    NetworkDetailRow("Local IP", status.localIpAddress ?: "Unknown")
                    NetworkDetailRow("Active Connections", status.activeConnections.size.toString())
                    NetworkDetailRow("VPN Status", if (status.isVpnActive) "Active" else "Inactive")
                }
            }
        }
    }
}

@Composable
private fun WifiInfoCard(wifiInfo: WifiSecurityInfo) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(
            containerColor = if (wifiInfo.isSecure)
                MaterialTheme.colorScheme.primaryContainer
            else
                MaterialTheme.colorScheme.errorContainer
        )
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                Icon(
                    imageVector = Icons.Default.Wifi,
                    contentDescription = null,
                    tint = if (wifiInfo.isSecure)
                        MaterialTheme.colorScheme.primary
                    else
                        MaterialTheme.colorScheme.error
                )
                Column {
                    Text(
                        text = wifiInfo.ssid ?: "Unknown Network",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold
                    )
                    Text(
                        text = wifiInfo.securityType.displayName,
                        style = MaterialTheme.typography.bodySmall
                    )
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(
                    text = "Signal: ${wifiInfo.signalStrength}/5",
                    style = MaterialTheme.typography.bodySmall
                )
                Text(
                    text = "${wifiInfo.linkSpeed} Mbps",
                    style = MaterialTheme.typography.bodySmall
                )
                Text(
                    text = "${wifiInfo.frequency} MHz",
                    style = MaterialTheme.typography.bodySmall
                )
            }
        }
    }
}

@Composable
private fun VpnStatusCard(isActive: Boolean) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(
            containerColor = if (isActive)
                MaterialTheme.colorScheme.primaryContainer
            else
                MaterialTheme.colorScheme.surfaceVariant
        )
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                Icon(
                    imageVector = Icons.Default.Shield,
                    contentDescription = null,
                    tint = if (isActive) Color(0xFF4CAF50) else MaterialTheme.colorScheme.onSurfaceVariant
                )
                Column {
                    Text(
                        text = "VPN Protection",
                        style = MaterialTheme.typography.titleSmall,
                        fontWeight = FontWeight.Medium
                    )
                    Text(
                        text = if (isActive) "Your connection is protected" else "No VPN detected",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }

            Icon(
                imageVector = if (isActive) Icons.Default.Check else Icons.Default.Close,
                contentDescription = null,
                tint = if (isActive) Color(0xFF4CAF50) else MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
    }
}

@Composable
private fun NetworkIssueCard(issue: NetworkIssue) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(
            containerColor = when (issue.severity) {
                NetworkIssueSeverity.CRITICAL -> MaterialTheme.colorScheme.errorContainer
                NetworkIssueSeverity.WARNING -> MaterialTheme.colorScheme.tertiaryContainer
                NetworkIssueSeverity.INFO -> MaterialTheme.colorScheme.secondaryContainer
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
                    NetworkIssueSeverity.CRITICAL -> Icons.Default.Close
                    NetworkIssueSeverity.WARNING -> Icons.Default.Warning
                    NetworkIssueSeverity.INFO -> Icons.Default.Info
                },
                contentDescription = null,
                tint = when (issue.severity) {
                    NetworkIssueSeverity.CRITICAL -> MaterialTheme.colorScheme.error
                    NetworkIssueSeverity.WARNING -> Color(0xFFF57C00)
                    NetworkIssueSeverity.INFO -> MaterialTheme.colorScheme.secondary
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
                Text(
                    text = issue.description,
                    style = MaterialTheme.typography.bodySmall
                )
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = issue.recommendation,
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }
    }
}

@Composable
private fun OpenPortItem(port: OpenPort) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Column {
            Text(
                text = "Port ${port.port}",
                style = MaterialTheme.typography.bodyMedium,
                fontWeight = FontWeight.Medium
            )
            Text(
                text = port.serviceName,
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }

        if (port.isCommonlyExploited) {
            Icon(
                imageVector = Icons.Default.Warning,
                contentDescription = "Commonly exploited",
                tint = Color(0xFFF57C00)
            )
        }
    }
}

@Composable
private fun NetworkDetailRow(label: String, value: String) {
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

@Preview(showBackground = true)
@Composable
private fun NetworkScanScreenPreview() {
    val mockStatus = NetworkSecurityStatus(
        wifiInfo = WifiSecurityInfo(
            ssid = "HomeNetwork",
            bssid = "AA:BB:CC:DD:EE:FF",
            securityType = WifiSecurityType.WPA2,
            signalStrength = 4,
            frequency = 5180,
            linkSpeed = 866,
            isSecure = true
        ),
        openPorts = listOf(
            OpenPort(port = 80, protocol = "TCP", serviceName = "HTTP", isCommonlyExploited = false),
            OpenPort(port = 443, protocol = "TCP", serviceName = "HTTPS", isCommonlyExploited = false)
        ),
        activeConnections = emptyList(),
        isVpnActive = false,
        localIpAddress = "192.168.1.100",
        overallScore = 78,
        issues = listOf(
            NetworkIssue(
                title = "No VPN Detected",
                description = "Your connection is not protected by a VPN",
                severity = NetworkIssueSeverity.WARNING,
                recommendation = "Consider using a VPN for enhanced privacy"
            )
        )
    )

    ShieldCheckTheme {
        NetworkScanScreen(
            uiState = NetworkScanUiState(
                isLoading = false,
                networkStatus = mockStatus
            ),
            onNavigateBack = {},
            onRescan = {},
            onClearError = {}
        )
    }
}
