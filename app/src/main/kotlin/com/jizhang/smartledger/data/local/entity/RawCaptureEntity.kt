package com.jizhang.smartledger.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

/** Room row for raw evidence captured before parsing. */
@Entity(tableName = "raw_captures")
data class RawCaptureEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val source: String,
    val appPackage: String?,
    val title: String?,
    val text: String,
    val imageUri: String?,
    val capturedAt: Long,
    val processedAt: Long?
)

