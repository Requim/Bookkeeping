package com.jizhang.smartledger.data.local.entity

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey

/** Room row for a confirmed ledger transaction. */
@Entity(
    tableName = "transactions",
    foreignKeys = [
        ForeignKey(
            entity = RawCaptureEntity::class,
            parentColumns = ["id"],
            childColumns = ["rawCaptureId"],
            onDelete = ForeignKey.SET_NULL
        ),
        ForeignKey(
            entity = CategoryEntity::class,
            parentColumns = ["id"],
            childColumns = ["categoryId"],
            onDelete = ForeignKey.NO_ACTION
        )
    ],
    indices = [Index("paidAt"), Index("rawCaptureId"), Index("categoryId")]
)
/** Room row for a confirmed ledger transaction. */
data class TransactionEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val amountCents: Long,
    val currency: String,
    val type: String,
    val merchant: String,
    val categoryId: Long,
    val paidAt: Long,
    val source: String,
    val rawCaptureId: Long?,
    val note: String,
    val createdAt: Long,
    val updatedAt: Long
)
