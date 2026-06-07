package com.jizhang.smartledger.data.remote.dto

import kotlinx.serialization.Serializable

/** Response wrapper for backend transaction lists. */
@Serializable
data class TransactionListResponseDto(
    val items: List<TransactionDto>
)

/** Backend confirmed transaction item shown in Android's recent ledger list. */
@Serializable
data class TransactionDto(
    val transactionId: String,
    val amountCents: Long,
    val currency: String = "CNY",
    val type: String,
    val merchant: String,
    val category: String,
    val paidAt: Long,
    val note: String = ""
)
