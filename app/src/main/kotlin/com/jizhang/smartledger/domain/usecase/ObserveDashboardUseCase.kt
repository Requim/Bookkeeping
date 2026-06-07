package com.jizhang.smartledger.domain.usecase

import com.jizhang.smartledger.domain.model.Transaction
import com.jizhang.smartledger.domain.model.TransactionDraft
import com.jizhang.smartledger.domain.repository.DraftRepository
import com.jizhang.smartledger.domain.repository.TransactionRepository
import com.jizhang.smartledger.domain.time.Clock
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.combine

/** Builds the dashboard read model from ledger repositories. */
class ObserveDashboardUseCase(
    private val draftRepository: DraftRepository,
    private val transactionRepository: TransactionRepository,
    private val clock: Clock
) {
    /** Emits today's expense total, pending drafts, and recent transactions. */
    operator fun invoke(): Flow<DashboardSummary> {
        val total = transactionRepository.observeExpenseTotal(
            clock.startOfTodayMillis(),
            clock.startOfTomorrowMillis()
        )
        return combine(
            total,
            draftRepository.observePending(),
            transactionRepository.observeRecent(limit = 20)
        ) { todayCents, drafts, transactions ->
            DashboardSummary(todayCents, drafts, transactions)
        }
    }
}

/** Read model rendered by the dashboard screen. */
data class DashboardSummary(
    val todayExpenseCents: Long,
    val pendingDrafts: List<TransactionDraft>,
    val recentTransactions: List<Transaction>
)

