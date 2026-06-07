package com.jizhang.smartledger.data.repository

import com.jizhang.smartledger.domain.model.Transaction
import com.jizhang.smartledger.domain.model.TransactionDraft
import com.jizhang.smartledger.domain.remote.RemoteLedgerGateway
import com.jizhang.smartledger.domain.repository.LedgerReadRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow

/** Remote-backed read repository used by the thin Android client. */
class RemoteLedgerReadRepository(
    private val gateway: RemoteLedgerGateway
) : LedgerReadRepository {
    private val pendingDrafts = MutableStateFlow<List<TransactionDraft>>(emptyList())
    private val recentTransactions = MutableStateFlow<List<Transaction>>(emptyList())
    private val todayExpenseCents = MutableStateFlow(0L)

    override fun observePendingDrafts(): Flow<List<TransactionDraft>> {
        return pendingDrafts
    }

    override fun observeRecentTransactions(limit: Int): Flow<List<Transaction>> {
        return recentTransactions
    }

    override fun observeTodayExpenseCents(): Flow<Long> {
        return todayExpenseCents
    }

    override suspend fun refresh() {
        pendingDrafts.value = gateway.pendingDrafts()
        recentTransactions.value = gateway.recentTransactions(DEFAULT_RECENT_LIMIT)
        todayExpenseCents.value = gateway.todaySummary().expenseCents
    }

    private companion object {
        const val DEFAULT_RECENT_LIMIT = 20
    }
}
