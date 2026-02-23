package com.fahim.shieldcheck.presentation.common.components

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.fahim.shieldcheck.ui.theme.ShieldCheckTheme

@Composable
fun SecurityScoreCard(
    score: Int,
    title: String,
    subtitle: String? = null,
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
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Text(
                text = title,
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold
            )

            CircularScoreIndicator(
                score = score,
                modifier = Modifier.size(120.dp)
            )

            subtitle?.let {
                Text(
                    text = it,
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }

            Text(
                text = getSecurityGrade(score),
                style = MaterialTheme.typography.headlineSmall,
                fontWeight = FontWeight.Bold,
                color = getScoreColor(score)
            )
        }
    }
}

@Composable
fun CircularScoreIndicator(
    score: Int,
    modifier: Modifier = Modifier,
    strokeWidth: Dp = 12.dp
) {
    var targetProgress by remember { mutableFloatStateOf(0f) }
    val animatedProgress by animateFloatAsState(
        targetValue = targetProgress,
        animationSpec = tween(durationMillis = 1000),
        label = "scoreAnimation"
    )

    LaunchedEffect(score) {
        targetProgress = score / 100f
    }

    val scoreColor = getScoreColor(score)
    val backgroundColor = MaterialTheme.colorScheme.surfaceVariant

    Box(
        modifier = modifier,
        contentAlignment = Alignment.Center
    ) {
        Canvas(modifier = Modifier.matchParentSize()) {
            val strokeWidthPx = strokeWidth.toPx()
            val arcSize = Size(size.width - strokeWidthPx, size.height - strokeWidthPx)
            val topLeft = Offset(strokeWidthPx / 2, strokeWidthPx / 2)

            // Background arc
            drawArc(
                color = backgroundColor,
                startAngle = -90f,
                sweepAngle = 360f,
                useCenter = false,
                topLeft = topLeft,
                size = arcSize,
                style = Stroke(width = strokeWidthPx, cap = StrokeCap.Round)
            )

            // Progress arc
            drawArc(
                color = scoreColor,
                startAngle = -90f,
                sweepAngle = 360f * animatedProgress,
                useCenter = false,
                topLeft = topLeft,
                size = arcSize,
                style = Stroke(width = strokeWidthPx, cap = StrokeCap.Round)
            )
        }

        Text(
            text = "$score",
            style = MaterialTheme.typography.headlineLarge,
            fontWeight = FontWeight.Bold,
            color = scoreColor
        )
    }
}

fun getScoreColor(score: Int): Color {
    return when {
        score >= 90 -> Color(0xFF4CAF50) // Green
        score >= 75 -> Color(0xFF8BC34A) // Light Green
        score >= 60 -> Color(0xFFFFC107) // Yellow
        score >= 40 -> Color(0xFFF57C00) // Orange
        else -> Color(0xFFD32F2F) // Red
    }
}

fun getSecurityGrade(score: Int): String {
    return when {
        score >= 90 -> "Grade: A"
        score >= 75 -> "Grade: B"
        score >= 60 -> "Grade: C"
        score >= 40 -> "Grade: D"
        else -> "Grade: F"
    }
}

@Preview(showBackground = true)
@Composable
private fun SecurityScoreCardPreview() {
    ShieldCheckTheme {
        SecurityScoreCard(
            score = 85,
            title = "Your Security Score",
            subtitle = "Based on apps, device, and network analysis"
        )
    }
}
