package com.jizhang.smartledger.data.local.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.jizhang.smartledger.data.local.entity.TransactionDraftEntity
import kotlinx.coroutines.flow.Flow

/** Database access for parsed transaction candidates. */
@Dao
interface DraftDao {
    /** Inserts a transaction candidate and returns its generated id. */
    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insert(entity: TransactionDraftEntity): Long

    /** Finds a draft by duplicate fingerprint. */
    @Query("SELECT * FROM transaction_drafts WHERE fingerprint = :fingerprint LIMIT 1")
    suspend fun findByFingerprint(fingerprint: String): TransactionDraftEntity?

    /** Finds a draft by primary key. */
    @Query("SELECT * FROM transaction_drafts WHERE id = :id LIMIT 1")
    suspend fun findById(id: Long): TransactionDraftEntity?

    /** Updates a draft review status. */
    @Query("UPDATE transaction_drafts SET status = :status WHERE id = :id")
    suspend fun updateStatus(id: Long, status: String)

    /** Emits pending transaction candidates newest first. */
    @Query("SELECT * FROM transaction_drafts WHERE status = 'PENDING' ORDER BY createdAt DESC")
    fun observePending(): Flow<List<TransactionDraftEntity>>
}

