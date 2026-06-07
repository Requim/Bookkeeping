package com.jizhang.smartledger.domain.model

/** Final confirmed bookkeeping record included in spending summaries. */
data class Transaction(
    val id: Long = 0,
    val money: Money,
    val type: TransactionType,
    val merchant: String,
    val categoryId: Long,
    val paidAt: Long,
    val source: CaptureSource,
    val rawCaptureId: Long?,
    val note: String = "",
    val createdAt: Long,
    val updatedAt: Long
)

