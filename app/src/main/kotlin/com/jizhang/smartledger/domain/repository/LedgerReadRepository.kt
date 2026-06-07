package com.jizhang.smartledger.domain.repository

import com.jizhang.smartledger.domain.model.Transaction
import com.jizhang.smartledger.domain.model.TransactionDraft
import kotlinx.coroutines.flow.Flow

/** Provides read models for the thin-client ledger UI. */
interface LedgerReadRepository {
    /** Emits pending drafts from the active ledger data source. */
    fun observePendingDrafts(): Flow<List<TransactionDraft>>

    /** Emits recent transactions from the active ledger data source. */
    fun observeRecentTransactions(limit: Int): Flow<List<Transaction>>

    /** Emits today's expense total from the active ledger data source. */
    fun observeTodayExpenseCents(): Flow<Long>

    /** Refreshes all dashboard data from the active ledger data source. */
    suspend fun refresh()
}
