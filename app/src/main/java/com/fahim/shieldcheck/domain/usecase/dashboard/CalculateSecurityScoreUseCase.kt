package com.fahim.shieldcheck.domain.usecase.dashboard

import com.fahim.shieldcheck.domain.model.dashboard.SecurityScore
import com.fahim.shieldcheck.domain.repository.PrivacyDashboardRepository
import javax.inject.Inject

class CalculateSecurityScoreUseCase @Inject constructor(
    private val privacyDashboardRepository: PrivacyDashboardRepository
) {
    suspend operator fun invoke(): SecurityScore {
        return privacyDashboardRepository.calculateOverallSecurityScore()
    }
}
