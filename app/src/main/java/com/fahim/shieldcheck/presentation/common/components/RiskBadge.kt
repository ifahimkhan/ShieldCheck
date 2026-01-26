package com.fahim.shieldcheck.presentation.common.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.fahim.shieldcheck.domain.model.app.RiskLevel

@Composable
fun RiskBadge(
    riskLevel: RiskLevel,
    modifier: Modifier = Modifier
) {
    val (backgroundColor, textColor) = when (riskLevel) {
        RiskLevel.CRITICAL -> Color(0xFFD32F2F) to Color.White
        RiskLevel.HIGH -> Color(0xFFF57C00) to Color.White
        RiskLevel.MEDIUM -> Color(0xFFFFC107) to Color.Black
        RiskLevel.LOW -> Color(0xFF4CAF50) to Color.White
        RiskLevel.SAFE -> Color(0xFF2196F3) to Color.White
    }

    Box(
        modifier = modifier
            .clip(RoundedCornerShape(4.dp))
            .background(backgroundColor)
            .padding(horizontal = 8.dp, vertical = 4.dp)
    ) {
        Text(
            text = riskLevel.name,
            style = MaterialTheme.typography.labelSmall,
            fontWeight = FontWeight.Bold,
            color = textColor
        )
    }
}

@Composable
fun RiskScoreBadge(
    score: Int,
    modifier: Modifier = Modifier
) {
    val riskLevel = when {
        score >= 75 -> RiskLevel.CRITICAL
        score >= 50 -> RiskLevel.HIGH
        score >= 25 -> RiskLevel.MEDIUM
        score >= 10 -> RiskLevel.LOW
        else -> RiskLevel.SAFE
    }
    RiskBadge(riskLevel = riskLevel, modifier = modifier)
}
