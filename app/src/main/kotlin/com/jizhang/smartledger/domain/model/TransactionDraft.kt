package com.jizhang.smartledger.domain.model

/** Parsed transaction candidate that requires user review before becoming a transaction. */
data class TransactionDraft(
    val id: Long = 0,
    val money: Money,
    val type: TransactionType,
    val merchant: String,
    val categoryId: Long?,
    val paidAt: Long,
    val source: CaptureSource,
    val confidence: Float,
    val fingerprint: String,
    val rawCaptureId: Long,
    val rawSummary: String,
    val status: DraftStatus = DraftStatus.PENDING,
    val createdAt: Long
)

/** User-editable values used when confirming a parsed transaction draft. */
data class ConfirmedDraftInput(
    val amountCents: Long,
    val type: TransactionType,
    val merchant: String,
    val categoryId: Long,
    val paidAt: Long,
    val note: String = ""
)

