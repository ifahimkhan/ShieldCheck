package com.fahim.shieldcheck.presentation.screens.privacydashboard

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
import androidx.compose.material.icons.filled.Apps
import androidx.compose.material.icons.filled.ChevronRight
import androidx.compose.material.icons.filled.PhoneAndroid
import androidx.compose.material.icons.filled.Refresh
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
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.fahim.shieldcheck.domain.model.dashboard.PrivacySummary
import com.fahim.shieldcheck.domain.model.dashboard.RecommendationAction
import com.fahim.shieldcheck.domain.model.dashboard.RecommendationPriority
import com.fahim.shieldcheck.domain.model.dashboard.SecurityRecommendation
import com.fahim.shieldcheck.presentation.common.components.LoadingIndicator
import com.fahim.shieldcheck.presentation.common.components.SecurityScoreCard
import java.text.SimpleDateFormat
import java.util.Locale

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PrivacyDashboardScreen(
    onNavigateBack: () -> Unit,
    onNavigateToAppAudit: () -> Unit,
    onNavigateToDeviceSecurity: () -> Unit,
    onNavigateToNetworkScan: () -> Unit,
    viewModel: PrivacyDashboardViewModel = hiltViewModel()
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
                title = { Text("Privacy Dashboard") },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back")
                    }
                },
                actions = {
                    IconButton(onClick = { viewModel.loadSummary() }) {
                        Icon(Icons.Default.Refresh, contentDescription = "Refresh")
                    }
                }
            )
        },
        snackbarHost = { SnackbarHost(snackbarHostState) }
    ) { paddingValues ->
        if (uiState.isLoading) {
            LoadingIndicator(message = "Loading dashboard...")
        } else {
            uiState.summary?.let { summary ->
                DashboardContent(
                    summary = summary,
                    onNavigateToAppAudit = onNavigateToAppAudit,
                    onNavigateToDeviceSecurity = onNavigateToDeviceSecurity,
                    onNavigateToNetworkScan = onNavigateToNetworkScan,
                    modifier = Modifier.padding(paddingValues)
                )
            }
        }
    }
}

@Composable
private fun DashboardContent(
    summary: PrivacySummary,
    onNavigateToAppAudit: () -> Unit,
    onNavigateToDeviceSecurity: () -> Unit,
    onNavigateToNetworkScan: () -> Unit,
    modifier: Modifier = Modifier
) {
    val dateFormat = remember { SimpleDateFormat("MMM dd, yyyy HH:mm", Locale.getDefault()) }

    LazyColumn(
        modifier = modifier.fillMaxSize(),
        contentPadding = PaddingValues(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // Overall Score
        item {
            SecurityScoreCard(
                score = summary.overallScore,
                title = "Overall Security Score",
                subtitle = summary.lastScanDate?.let { "Last scan: ${dateFormat.format(it)}" }
            )
        }

        // Score Breakdown
        item {
            Text(
                text = "Security Breakdown",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold
            )
        }

        item {
            ScoreBreakdownCards(
                appScore = (100 - summary.appRiskScore).toInt(),
                deviceScore = summary.deviceSecurityScore,
                networkScore = summary.networkSecurityScore,
                onNavigateToAppAudit = onNavigateToAppAudit,
                onNavigateToDeviceSecurity = onNavigateToDeviceSecurity,
                onNavigateToNetworkScan = onNavigateToNetworkScan
            )
        }

        // Quick Stats
        item {
            QuickStatsCard(summary = summary)
        }

        // Recommendations
        if (summary.recommendations.isNotEmpty()) {
            item {
                Text(
                    text = "Recommendations",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold
                )
            }

            items(summary.recommendations) { recommendation ->
                RecommendationCard(
                    recommendation = recommendation,
                    onClick = {
                        when (recommendation.actionType) {
                            RecommendationAction.REVIEW_APPS -> onNavigateToAppAudit()
                            RecommendationAction.DEVICE_SETTINGS -> onNavigateToDeviceSecurity()
                            RecommendationAction.NETWORK_SETTINGS -> onNavigateToNetworkScan()
                            RecommendationAction.GENERAL -> {}
                        }
                    }
                )
            }
        }
    }
}

@Composable
private fun ScoreBreakdownCards(
    appScore: Int,
    deviceScore: Int,
    networkScore: Int,
    onNavigateToAppAudit: () -> Unit,
    onNavigateToDeviceSecurity: () -> Unit,
    onNavigateToNetworkScan: () -> Unit
) {
    Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
        ScoreCard(
            title = "App Security",
            score = appScore,
            icon = Icons.Default.Apps,
            onClick = onNavigateToAppAudit
        )
        ScoreCard(
            title = "Device Security",
            score = deviceScore,
            icon = Icons.Default.PhoneAndroid,
            onClick = onNavigateToDeviceSecurity
        )
        ScoreCard(
            title = "Network Security",
            score = networkScore,
            icon = Icons.Default.Wifi,
            onClick = onNavigateToNetworkScan
        )
    }
}

@Composable
private fun ScoreCard(
    title: String,
    score: Int,
    icon: ImageVector,
    onClick: () -> Unit
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        onClick = onClick
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                imageVector = icon,
                contentDescription = null,
                tint = getScoreColor(score)
            )

            Column(
                modifier = Modifier
                    .weight(1f)
                    .padding(horizontal = 12.dp)
            ) {
                Text(
                    text = title,
                    style = MaterialTheme.typography.titleSmall,
                    fontWeight = FontWeight.Medium
                )
                Text(
                    text = "$score/100",
                    style = MaterialTheme.typography.bodySmall,
                    color = getScoreColor(score)
                )
            }

            Icon(
                imageVector = Icons.Default.ChevronRight,
                contentDescription = "View details",
                tint = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
    }
}

@Composable
private fun QuickStatsCard(summary: PrivacySummary) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
        ) {
            Text(
                text = "Quick Stats",
                style = MaterialTheme.typography.titleSmall,
                fontWeight = FontWeight.Bold
            )

            Spacer(modifier = Modifier.height(12.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceEvenly
            ) {
                StatItem(
                    value = summary.totalAppsScanned.toString(),
                    label = "Apps Scanned"
                )
                StatItem(
                    value = summary.criticalAppsCount.toString(),
                    label = "Critical Apps",
                    valueColor = if (summary.criticalAppsCount > 0) Color(0xFFD32F2F) else null
                )
                StatItem(
                    value = summary.totalIssues.toString(),
                    label = "Total Issues",
                    valueColor = if (summary.totalIssues > 0) Color(0xFFF57C00) else null
                )
            }
        }
    }
}

@Composable
private fun StatItem(
    value: String,
    label: String,
    valueColor: Color? = null
) {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Text(
            text = value,
            style = MaterialTheme.typography.headlineSmall,
            fontWeight = FontWeight.Bold,
            color = valueColor ?: MaterialTheme.colorScheme.onSurface
        )
        Text(
            text = label,
            style = MaterialTheme.typography.labelSmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
    }
}

@Composable
private fun RecommendationCard(
    recommendation: SecurityRecommendation,
    onClick: () -> Unit
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        onClick = onClick,
        colors = CardDefaults.cardColors(
            containerColor = when (recommendation.priority) {
                RecommendationPriority.HIGH -> MaterialTheme.colorScheme.errorContainer.copy(alpha = 0.5f)
                RecommendationPriority.MEDIUM -> MaterialTheme.colorScheme.tertiaryContainer.copy(alpha = 0.5f)
                RecommendationPriority.LOW -> MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.5f)
            }
        )
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = recommendation.title,
                    style = MaterialTheme.typography.titleSmall,
                    fontWeight = FontWeight.Medium
                )
                Text(
                    text = recommendation.description,
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }

            if (recommendation.actionType != RecommendationAction.GENERAL) {
                Icon(
                    imageVector = Icons.Default.ChevronRight,
                    contentDescription = "Take action",
                    tint = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }
    }
}

private fun getScoreColor(score: Int): Color {
    return when {
        score >= 90 -> Color(0xFF4CAF50)
        score >= 75 -> Color(0xFF8BC34A)
        score >= 60 -> Color(0xFFFFC107)
        score >= 40 -> Color(0xFFF57C00)
        else -> Color(0xFFD32F2F)
    }
}
