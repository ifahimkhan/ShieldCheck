package com.fahim.shieldcheck.domain.usecase.dashboard

import com.fahim.shieldcheck.domain.model.dashboard.PrivacySummary
import com.fahim.shieldcheck.domain.repository.PrivacyDashboardRepository
import javax.inject.Inject

class GetPrivacySummaryUseCase @Inject constructor(
    private val privacyDashboardRepository: PrivacyDashboardRepository
) {
    suspend operator fun invoke(): PrivacySummary {
        return privacyDashboardRepository.getPrivacySummary()
    }
}
