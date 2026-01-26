package com.fahim.shieldcheck.presentation.screens.appaudit

import androidx.compose.foundation.Image
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
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Android
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Divider
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
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
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.core.graphics.drawable.toBitmap
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.fahim.shieldcheck.domain.model.app.AppPermission
import com.fahim.shieldcheck.domain.model.app.InstalledApp
import com.fahim.shieldcheck.domain.model.app.RiskLevel
import com.fahim.shieldcheck.presentation.common.components.ExpandableCard
import com.fahim.shieldcheck.presentation.common.components.LoadingIndicator
import com.fahim.shieldcheck.presentation.common.components.RiskBadge
import java.text.SimpleDateFormat
import java.util.Locale

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AppDetailScreen(
    packageName: String,
    onNavigateBack: () -> Unit,
    viewModel: AppDetailViewModel = hiltViewModel()
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
                title = { Text(uiState.app?.appName ?: "App Details") },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back")
                    }
                }
            )
        },
        snackbarHost = { SnackbarHost(snackbarHostState) }
    ) { paddingValues ->
        when {
            uiState.isLoading -> {
                LoadingIndicator(message = "Loading app details...")
            }
            uiState.app != null -> {
                AppDetailContent(
                    app = uiState.app!!,
                    modifier = Modifier.padding(paddingValues)
                )
            }
            else -> {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(paddingValues),
                    contentAlignment = Alignment.Center
                ) {
                    Text("App not found")
                }
            }
        }
    }
}

@Composable
private fun AppDetailContent(
    app: InstalledApp,
    modifier: Modifier = Modifier
) {
    val dateFormat = remember { SimpleDateFormat("MMM dd, yyyy", Locale.getDefault()) }

    LazyColumn(
        modifier = modifier.fillMaxSize(),
        contentPadding = PaddingValues(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // App Header
        item {
            AppHeaderCard(app = app, dateFormat = dateFormat)
        }

        // Risk Summary
        item {
            RiskSummaryCard(app = app)
        }

        // Dangerous Permissions
        if (app.dangerousPermissions.isNotEmpty()) {
            item {
                ExpandableCard(
                    title = "Dangerous Permissions",
                    subtitle = "${app.dangerousPermissionCount} permissions require attention",
                    initialExpanded = true
                ) {
                    Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                        app.dangerousPermissions.forEach { permission ->
                            PermissionItem(permission = permission)
                        }
                    }
                }
            }
        }

        // All Permissions
        item {
            ExpandableCard(
                title = "All Permissions",
                subtitle = "${app.totalPermissionCount} total permissions"
            ) {
                Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    app.permissions.forEach { permission ->
                        PermissionItem(permission = permission)
                        if (app.permissions.last() != permission) {
                            HorizontalDivider()
                        }
                    }
                }
            }
        }

        // Package Info
        item {
            ExpandableCard(
                title = "Package Information"
            ) {
                Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    InfoRow("Package Name", app.packageName)
                    InfoRow("Version", app.versionName ?: "Unknown")
                    InfoRow("Version Code", app.versionCode.toString())
                    InfoRow("Type", if (app.isSystemApp) "System App" else "User App")
                    app.installedDate?.let {
                        InfoRow("Installed", dateFormat.format(it))
                    }
                    app.lastUpdatedDate?.let {
                        InfoRow("Last Updated", dateFormat.format(it))
                    }
                }
            }
        }
    }
}

@Composable
private fun AppHeaderCard(
    app: InstalledApp,
    dateFormat: SimpleDateFormat
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            if (app.icon != null) {
                Image(
                    bitmap = app.icon.toBitmap().asImageBitmap(),
                    contentDescription = app.appName,
                    modifier = Modifier
                        .size(72.dp)
                        .clip(RoundedCornerShape(12.dp))
                )
            } else {
                Icon(
                    imageVector = Icons.Default.Android,
                    contentDescription = app.appName,
                    modifier = Modifier.size(72.dp),
                    tint = MaterialTheme.colorScheme.primary
                )
            }

            Spacer(modifier = Modifier.width(16.dp))

            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = app.appName,
                    style = MaterialTheme.typography.headlineSmall,
                    fontWeight = FontWeight.Bold
                )
                Text(
                    text = app.versionName ?: "Unknown version",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
                if (app.isSystemApp) {
                    Text(
                        text = "System App",
                        style = MaterialTheme.typography.labelSmall,
                        color = MaterialTheme.colorScheme.secondary
                    )
                }
            }

            RiskBadge(riskLevel = app.riskLevel)
        }
    }
}

@Composable
private fun RiskSummaryCard(app: InstalledApp) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(
            containerColor = when (app.riskLevel) {
                RiskLevel.CRITICAL -> MaterialTheme.colorScheme.errorContainer
                RiskLevel.HIGH -> MaterialTheme.colorScheme.errorContainer.copy(alpha = 0.7f)
                RiskLevel.MEDIUM -> MaterialTheme.colorScheme.tertiaryContainer
                else -> MaterialTheme.colorScheme.primaryContainer
            }
        )
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                if (app.riskLevel == RiskLevel.CRITICAL || app.riskLevel == RiskLevel.HIGH) {
                    Icon(
                        imageVector = Icons.Default.Warning,
                        contentDescription = null,
                        tint = MaterialTheme.colorScheme.error
                    )
                }
                Text(
                    text = "Risk Score: ${app.riskScore}/100",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold
                )
            }

            Spacer(modifier = Modifier.height(8.dp))

            Text(
                text = getRiskDescription(app.riskLevel, app.dangerousPermissionCount),
                style = MaterialTheme.typography.bodyMedium
            )
        }
    }
}

@Composable
private fun PermissionItem(permission: AppPermission) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = permission.displayName,
                style = MaterialTheme.typography.bodyMedium,
                fontWeight = FontWeight.Medium
            )
            Text(
                text = permission.description,
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
        RiskBadge(riskLevel = permission.riskLevel)
    }
}

@Composable
private fun InfoRow(label: String, value: String) {
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

private fun getRiskDescription(riskLevel: RiskLevel, dangerousCount: Int): String {
    return when (riskLevel) {
        RiskLevel.CRITICAL -> "This app has critical permissions that could compromise your privacy and security. Review carefully."
        RiskLevel.HIGH -> "This app requests $dangerousCount dangerous permissions. Consider whether all are necessary."
        RiskLevel.MEDIUM -> "This app has some permissions that warrant attention but poses moderate risk."
        RiskLevel.LOW -> "This app requests minimal sensitive permissions."
        RiskLevel.SAFE -> "This app has a low risk profile with no dangerous permissions."
    }
}
