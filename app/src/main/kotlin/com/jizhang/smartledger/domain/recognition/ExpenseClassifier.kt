package com.jizhang.smartledger.domain.recognition

/** Assigns a spending category from merchant and raw payment text. */
interface ExpenseClassifier {
    /** Returns a category id or null when no confident local rule matches. */
    suspend fun classify(merchant: String, rawText: String): Long?
}

