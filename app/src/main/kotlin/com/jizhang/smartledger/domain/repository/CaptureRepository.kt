package com.jizhang.smartledger.domain.repository

import com.jizhang.smartledger.domain.model.RawCapture
import kotlinx.coroutines.flow.Flow

/** Persists and exposes raw evidence captured before parsing. */
interface CaptureRepository {
    /** Stores raw evidence and returns its generated identifier. */
    suspend fun save(capture: RawCapture): Long

    /** Marks a raw capture as processed at the provided epoch milliseconds. */
    suspend fun markProcessed(id: Long, processedAt: Long)

    /** Emits recent raw captures for diagnostics and audit screens. */
    fun recent(limit: Int): Flow<List<RawCapture>>
}

