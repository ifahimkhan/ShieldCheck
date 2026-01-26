package com.fahim.shieldcheck.domain.model.app

enum class RiskLevel {
    CRITICAL,
    HIGH,
    MEDIUM,
    LOW,
    SAFE;

    companion object {
        fun fromScore(score: Int): RiskLevel {
            return when {
                score >= 75 -> CRITICAL
                score >= 50 -> HIGH
                score >= 25 -> MEDIUM
                score >= 10 -> LOW
                else -> SAFE
            }
        }

        fun fromString(value: String): RiskLevel {
            return try {
                valueOf(value.uppercase())
            } catch (e: IllegalArgumentException) {
                SAFE
            }
        }
    }
}
