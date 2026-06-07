package com.jizhang.smartledger.data.repository

import com.jizhang.smartledger.data.local.dao.CaptureDao
import com.jizhang.smartledger.data.local.toDomain
import com.jizhang.smartledger.data.local.toEntity
import com.jizhang.smartledger.domain.model.RawCapture
import com.jizhang.smartledger.domain.repository.CaptureRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

/** Room-backed implementation for raw capture persistence. */
class RoomCaptureRepository(
    private val dao: CaptureDao
) : CaptureRepository {
    override suspend fun save(capture: RawCapture): Long {
        return dao.insert(capture.toEntity())
    }

    override suspend fun markProcessed(id: Long, processedAt: Long) {
        dao.markProcessed(id, processedAt)
    }

    override fun recent(limit: Int): Flow<List<RawCapture>> {
        return dao.recent(limit).map { rows -> rows.map { it.toDomain() } }
    }
}

