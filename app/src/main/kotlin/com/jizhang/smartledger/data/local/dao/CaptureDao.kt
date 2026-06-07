package com.jizhang.smartledger.data.local.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import com.jizhang.smartledger.data.local.entity.RawCaptureEntity
import kotlinx.coroutines.flow.Flow

/** Database access for raw capture evidence. */
@Dao
interface CaptureDao {
    /** Inserts raw evidence and returns its generated id. */
    @Insert
    suspend fun insert(entity: RawCaptureEntity): Long

    /** Updates the processed timestamp for raw evidence. */
    @Query("UPDATE raw_captures SET processedAt = :processedAt WHERE id = :id")
    suspend fun markProcessed(id: Long, processedAt: Long)

    /** Emits recent raw evidence rows for diagnostics. */
    @Query("SELECT * FROM raw_captures ORDER BY capturedAt DESC LIMIT :limit")
    fun recent(limit: Int): Flow<List<RawCaptureEntity>>
}

