package com.jizhang.smartledger.data.repository

import com.jizhang.smartledger.data.local.dao.TransactionDao
import com.jizhang.smartledger.data.local.toDomain
import com.jizhang.smartledger.data.local.toEntity
import com.jizhang.smartledger.domain.model.Transaction
import com.jizhang.smartledger.domain.repository.TransactionRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

/** Room-backed implementation for confirmed ledger transactions. */
class RoomTransactionRepository(
    private val dao: TransactionDao
) : TransactionRepository {
    override suspend fun save(transaction: Transaction): Long {
        return dao.insert(transaction.toEntity())
    }

    override fun observeRecent(limit: Int): Flow<List<Transaction>> {
        return dao.observeRecent(limit).map { rows -> rows.map { it.toDomain() } }
    }

    override fun observeExpenseTotal(
        startInclusive: Long,
        endExclusive: Long
    ): Flow<Long> {
        return dao.observeExpenseTotal(startInclusive, endExclusive)
    }
}

