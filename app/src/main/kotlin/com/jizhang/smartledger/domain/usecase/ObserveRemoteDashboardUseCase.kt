package com.jizhang.smartledger.domain.usecase

import com.jizhang.smartledger.domain.repository.LedgerReadRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.combine

/** Builds dashboard state from the remote ledger read repository. */
class ObserveRemoteDashboardUseCase(
    private val ledgerReadRepository: LedgerReadRepository
) {
    /** Emits today's total, pending drafts, and recent transactions from FastAPI. */
    operator fun invoke(): Flow<DashboardSummary> {
        return combine(
            ledgerReadRepository.observeTodayExpenseCents(),
            ledgerReadRepository.observePendingDrafts(),
            ledgerReadRepository.observeRecentTransactions(limit = 20)
        ) { todayCents, drafts, transactions ->
            DashboardSummary(todayCents, drafts, transactions)
        }
    }
}
