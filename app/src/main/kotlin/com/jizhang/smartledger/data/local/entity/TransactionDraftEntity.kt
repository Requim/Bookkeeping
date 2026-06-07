package com.jizhang.smartledger.data.local.entity

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey

/** Room row for a parsed transaction candidate awaiting review. */
@Entity(
    tableName = "transaction_drafts",
    foreignKeys = [
        ForeignKey(
            entity = RawCaptureEntity::class,
            parentColumns = ["id"],
            childColumns = ["rawCaptureId"],
            onDelete = ForeignKey.CASCADE
        ),
        ForeignKey(
            entity = CategoryEntity::class,
            parentColumns = ["id"],
            childColumns = ["categoryId"],
            onDelete = ForeignKey.SET_NULL
        )
    ],
    indices = [
        Index("rawCaptureId"),
        Index("categoryId"),
        Index(value = ["fingerprint"], unique = true)
    ]
)
/** Room row for a parsed transaction candidate awaiting review. */
data class TransactionDraftEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val amountCents: Long,
    val currency: String,
    val type: String,
    val merchant: String,
    val categoryId: Long?,
    val paidAt: Long,
    val source: String,
    val confidence: Float,
    val fingerprint: String,
    val rawCaptureId: Long,
    val rawSummary: String,
    val status: String,
    val createdAt: Long
)
