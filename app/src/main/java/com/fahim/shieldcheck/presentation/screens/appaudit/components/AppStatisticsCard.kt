package com.fahim.shieldcheck.presentation.screens.appaudit.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.fahim.shieldcheck.domain.usecase.app.AppStatistics

@Composable
fun AppStatisticsCard(
    statistics: AppStatistics,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier.fillMaxWidth(),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Text(
                text = "App Risk Overview",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold
            )

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceEvenly
            ) {
                StatItem(
                    count = statistics.criticalCount,
                    label = "Critical",
                    color = Color(0xFFD32F2F)
                )
                StatItem(
                    count = statistics.highRiskCount,
                    label = "High",
                    color = Color(0xFFF57C00)
                )
                StatItem(
                    count = statistics.mediumRiskCount,
                    label = "Medium",
                    color = Color(0xFFFFC107)
                )
                StatItem(
                    count = statistics.lowRiskCount,
                    label = "Low",
                    color = Color(0xFF4CAF50)
                )
                StatItem(
                    count = statistics.safeCount,
                    label = "Safe",
                    color = Color(0xFF2196F3)
                )
            }

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(
                    text = "Total Apps: ${statistics.totalApps}",
                    style = MaterialTheme.typography.bodyMedium
                )
                Text(
                    text = "Avg Risk: ${String.format("%.1f", statistics.averageRiskScore)}",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }
    }
}

@Composable
private fun StatItem(
    count: Int,
    label: String,
    color: Color
) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = count.toString(),
            style = MaterialTheme.typography.headlineSmall,
            fontWeight = FontWeight.Bold,
            color = color
        )
        Text(
            text = label,
            style = MaterialTheme.typography.labelSmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
    }
}
