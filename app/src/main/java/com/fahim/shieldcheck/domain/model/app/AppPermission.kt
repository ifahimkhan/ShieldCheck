package com.fahim.shieldcheck.domain.model.app

data class AppPermission(
    val name: String,
    val description: String,
    val riskScore: Int,
    val riskLevel: RiskLevel,
    val isGranted: Boolean,
    val isDangerous: Boolean
) {
    val displayName: String
        get() = name.substringAfterLast('.').replace('_', ' ')
            .lowercase().replaceFirstChar { it.uppercase() }
}
