package com.jizhang.smartledger.domain.repository

import com.jizhang.smartledger.domain.model.Transaction
import kotlinx.coroutines.flow.Flow

/** Stores confirmed ledger transactions and produces spending summaries. */
interface TransactionRepository {
    /** Inserts a confirmed transaction and returns its generated identifier. */
    suspend fun save(transaction: Transaction): Long

    /** Emits recent confirmed transactions ordered by paid time descending. */
    fun observeRecent(limit: Int): Flow<List<Transaction>>

    /** Emits expense total between startInclusive and endExclusive epoch milliseconds. */
    fun observeExpenseTotal(startInclusive: Long, endExclusive: Long): Flow<Long>
}

