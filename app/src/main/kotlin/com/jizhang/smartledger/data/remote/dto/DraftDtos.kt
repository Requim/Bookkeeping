package com.jizhang.smartledger.data.remote.dto

import kotlinx.serialization.Serializable

/** Response wrapper for backend draft lists. */
@Serializable
data class DraftListResponseDto(
    val items: List<DraftDto>
)

/** Backend draft item shown in Android's pending review list. */
@Serializable
data class DraftDto(
    val draftId: String,
    val amountCents: Long,
    val currency: String = "CNY",
    val type: String,
    val merchant: String,
    val category: String? = null,
    val paidAt: Long,
    val confidence: Float,
    val status: String
)

/** Request body used when a user confirms a backend draft. */
@Serializable
data class ConfirmDraftRequestDto(
    val amountCents: Long,
    val currency: String,
    val type: String,
    val merchant: String,
    val category: String,
    val paidAt: Long,
    val note: String
)

/** Response body returned after confirming a backend draft. */
@Serializable
data class ConfirmDraftResponseDto(
    val transactionId: String,
    val status: String
)

/** Response body returned after ignoring a backend draft. */
@Serializable
data class IgnoreDraftResponseDto(
    val draftId: String,
    val status: String
)
