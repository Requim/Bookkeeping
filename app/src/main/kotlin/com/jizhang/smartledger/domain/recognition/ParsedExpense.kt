package com.jizhang.smartledger.domain.recognition

import com.jizhang.smartledger.domain.model.Money
import com.jizhang.smartledger.domain.model.TransactionType

/** Parsed transaction data before duplicate detection and category classification. */
data class ParsedExpense(
    val money: Money,
    val type: TransactionType,
    val merchant: String,
    val paidAt: Long,
    val confidence: Float,
    val rawSummary: String
)

