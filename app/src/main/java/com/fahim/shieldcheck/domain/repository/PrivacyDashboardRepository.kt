package com.fahim.shieldcheck.domain.repository

import com.fahim.shieldcheck.domain.model.dashboard.PrivacySummary
import com.fahim.shieldcheck.domain.model.dashboard.SecurityScore
import kotlinx.coroutines.flow.Flow

interface PrivacyDashboardRepository {
    suspend fun getPrivacySummary(): PrivacySummary
    fun observePrivacySummary(): Flow<PrivacySummary>
    suspend fun calculateOverallSecurityScore(): SecurityScore
    suspend fun refreshAllScans()
}
