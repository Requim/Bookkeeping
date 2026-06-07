package com.jizhang.smartledger.domain.repository

import com.jizhang.smartledger.domain.model.DraftStatus
import com.jizhang.smartledger.domain.model.TransactionDraft
import kotlinx.coroutines.flow.Flow

/** Manages parsed transaction candidates before user confirmation. */
interface DraftRepository {
    /** Inserts a new candidate and returns its generated identifier. */
    suspend fun save(draft: TransactionDraft): Long

    /** Finds a draft by its duplicate-detection fingerprint. */
    suspend fun findByFingerprint(fingerprint: String): TransactionDraft?

    /** Loads a draft by identifier or returns null when it is missing. */
    suspend fun findById(id: Long): TransactionDraft?

    /** Updates the review status for a draft. */
    suspend fun updateStatus(id: Long, status: DraftStatus)

    /** Emits pending drafts ordered by newest first. */
    fun observePending(): Flow<List<TransactionDraft>>
}

