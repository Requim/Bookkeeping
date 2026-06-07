package com.jizhang.smartledger.data.repository

import com.jizhang.smartledger.data.local.dao.DraftDao
import com.jizhang.smartledger.data.local.toDomain
import com.jizhang.smartledger.data.local.toEntity
import com.jizhang.smartledger.domain.model.DraftStatus
import com.jizhang.smartledger.domain.model.TransactionDraft
import com.jizhang.smartledger.domain.repository.DraftRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

/** Room-backed implementation for pending transaction candidates. */
class RoomDraftRepository(
    private val dao: DraftDao
) : DraftRepository {
    override suspend fun save(draft: TransactionDraft): Long {
        return dao.insert(draft.toEntity())
    }

    override suspend fun findByFingerprint(fingerprint: String): TransactionDraft? {
        return dao.findByFingerprint(fingerprint)?.toDomain()
    }

    override suspend fun findById(id: Long): TransactionDraft? {
        return dao.findById(id)?.toDomain()
    }

    override suspend fun updateStatus(id: Long, status: DraftStatus) {
        dao.updateStatus(id, status.name)
    }

    override fun observePending(): Flow<List<TransactionDraft>> {
        return dao.observePending().map { rows -> rows.map { it.toDomain() } }
    }
}

