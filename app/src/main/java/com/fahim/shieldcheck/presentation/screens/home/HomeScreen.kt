package com.fahim.shieldcheck.presentation.screens.home

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Apps
import androidx.compose.material.icons.filled.ChevronRight
import androidx.compose.material.icons.filled.Dashboard
import androidx.compose.material.icons.filled.PhoneAndroid
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.filled.Shield
import androidx.compose.material.icons.filled.Wifi
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.getValue
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver
import androidx.lifecycle.compose.LocalLifecycleOwner
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.fahim.shieldcheck.presentation.common.components.SecurityScoreCard
import com.fahim.shieldcheck.ui.theme.ShieldCheckTheme

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreen(
    onNavigateToAppAudit: () -> Unit,
    onNavigateToDeviceSecurity: () -> Unit,
    onNavigateToNetworkScan: () -> Unit,
    onNavigateToPrivacyDashboard: () -> Unit,
    onNavigateToSettings: () -> Unit,
    viewModel: HomeViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    val lifecycleOwner = LocalLifecycleOwner.current

    DisposableEffect(lifecycleOwner) {
        val observer = LifecycleEventObserver { _, event ->
            if (event == Lifecycle.Event.ON_RESUME) {
                viewModel.loadSecurityScore()
            }
        }
        lifecycleOwner.lifecycle.addObserver(observer)
        onDispose {
            lifecycleOwner.lifecycle.removeObserver(observer)
        }
    }

    HomeScreenContent(
        uiState = uiState,
        onNavigateToAppAudit = onNavigateToAppAudit,
        onNavigateToDeviceSecurity = onNavigateToDeviceSecurity,
        onNavigateToNetworkScan = onNavigateToNetworkScan,
        onNavigateToPrivacyDashboard = onNavigateToPrivacyDashboard,
        onNavigateToSettings = onNavigateToSettings
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreenContent(
    uiState: HomeUiState,
    onNavigateToAppAudit: () -> Unit,
    onNavigateToDeviceSecurity: () -> Unit,
    onNavigateToNetworkScan: () -> Unit,
    onNavigateToPrivacyDashboard: () -> Unit,
    onNavigateToSettings: () -> Unit
) {
    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            imageVector = Icons.Default.Shield,
                            contentDescription = null,
                            tint = MaterialTheme.colorScheme.primary,
                            modifier = Modifier.size(28.dp)
                        )
                        Text(
                            text = "ShieldCheck",
                            modifier = Modifier.padding(start = 8.dp)
                        )
                    }
                },
                actions = {
                    IconButton(onClick = onNavigateToSettings) {
                        Icon(Icons.Default.Settings, contentDescription = "Settings")
                    }
                }
            )
        }
    ) { paddingValues ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues),
            contentPadding = PaddingValues(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // Welcome Section
            item {
                WelcomeSection()
            }

            // Security Score
            item {
                uiState.securityScore?.let { score ->
                    SecurityScoreCard(
                        score = score.score,
                        title = "Your Security Score",
                        subtitle = "Based on apps, device, and network analysis"
                    )
                } ?: run {
                    SecurityScoreCard(
                        score = 0,
                        title = "Your Security Score",
                        subtitle = "Run a scan to calculate your score"
                    )
                }
            }

            // Feature Cards
            item {
                Text(
                    text = "Security Features",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold
                )
            }

            item {
                FeatureCard(
                    icon = Icons.Default.Apps,
                    title = "App Permission Audit",
                    description = "Scan installed apps and analyze their permissions",
                    iconColor = Color(0xFF2196F3),
                    onClick = onNavigateToAppAudit
                )
            }

            item {
                FeatureCard(
                    icon = Icons.Default.PhoneAndroid,
                    title = "Device Security",
                    description = "Check root status, encryption, and security settings",
                    iconColor = Color(0xFF4CAF50),
                    onClick = onNavigateToDeviceSecurity
                )
            }

            item {
                FeatureCard(
                    icon = Icons.Default.Wifi,
                    title = "Network Scanner",
                    description = "Analyze WiFi security and detect open ports",
                    iconColor = Color(0xFFF57C00),
                    onClick = onNavigateToNetworkScan
                )
            }

            item {
                FeatureCard(
                    icon = Icons.Default.Dashboard,
                    title = "Privacy Dashboard",
                    description = "View comprehensive security overview and recommendations",
                    iconColor = Color(0xFF9C27B0),
                    onClick = onNavigateToPrivacyDashboard
                )
            }

            // Bottom spacing
            item {
                Spacer(modifier = Modifier.height(16.dp))
            }
        }
    }
}

@Composable
private fun WelcomeSection() {
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.primaryContainer
        )
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(20.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Icon(
                imageVector = Icons.Default.Shield,
                contentDescription = null,
                modifier = Modifier.size(48.dp),
                tint = MaterialTheme.colorScheme.primary
            )
            Spacer(modifier = Modifier.height(12.dp))
            Text(
                text = "Welcome to ShieldCheck",
                style = MaterialTheme.typography.headlineSmall,
                fontWeight = FontWeight.Bold,
                textAlign = TextAlign.Center
            )
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = "Your personal security companion for Android",
                style = MaterialTheme.typography.bodyMedium,
                textAlign = TextAlign.Center,
                color = MaterialTheme.colorScheme.onPrimaryContainer.copy(alpha = 0.8f)
            )
        }
    }
}

@Composable
private fun FeatureCard(
    icon: ImageVector,
    title: String,
    description: String,
    iconColor: Color,
    onClick: () -> Unit
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        onClick = onClick,
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Card(
                colors = CardDefaults.cardColors(
                    containerColor = iconColor.copy(alpha = 0.1f)
                )
            ) {
                Icon(
                    imageVector = icon,
                    contentDescription = null,
                    modifier = Modifier
                        .padding(12.dp)
                        .size(32.dp),
                    tint = iconColor
                )
            }

            Column(
                modifier = Modifier
                    .weight(1f)
                    .padding(horizontal = 16.dp)
            ) {
                Text(
                    text = title,
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.SemiBold
                )
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = description,
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }

            Icon(
                imageVector = Icons.Default.ChevronRight,
                contentDescription = "Open",
                tint = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
    }
}

@Preview(showBackground = true)
@Composable
private fun HomeScreenPreview() {
    ShieldCheckTheme {
        HomeScreenContent(
            uiState = HomeUiState(),
            onNavigateToAppAudit = {},
            onNavigateToDeviceSecurity = {},
            onNavigateToNetworkScan = {},
            onNavigateToPrivacyDashboard = {},
            onNavigateToSettings = {}
        )
    }
}
