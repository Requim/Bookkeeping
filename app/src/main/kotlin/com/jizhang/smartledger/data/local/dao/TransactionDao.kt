package com.jizhang.smartledger.data.local.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import com.jizhang.smartledger.data.local.entity.TransactionEntity
import kotlinx.coroutines.flow.Flow

/** Database access for confirmed transactions and totals. */
@Dao
interface TransactionDao {
    /** Inserts a confirmed transaction and returns its generated id. */
    @Insert
    suspend fun insert(entity: TransactionEntity): Long

    /** Emits recent confirmed transactions ordered by paid time. */
    @Query("SELECT * FROM transactions ORDER BY paidAt DESC LIMIT :limit")
    fun observeRecent(limit: Int): Flow<List<TransactionEntity>>

    /** Emits expense total in cents within the provided time window. */
    @Query(
        """
        SELECT COALESCE(SUM(amountCents), 0)
        FROM transactions
        WHERE type = 'EXPENSE' AND paidAt >= :startInclusive AND paidAt < :endExclusive
        """
    )
    fun observeExpenseTotal(startInclusive: Long, endExclusive: Long): Flow<Long>
}

