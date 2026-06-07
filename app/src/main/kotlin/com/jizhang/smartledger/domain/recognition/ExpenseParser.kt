package com.jizhang.smartledger.domain.recognition

/** Parses raw payment text into a normalized expense candidate. */
interface ExpenseParser {
    /** Returns a parsed expense or null when text does not look like a transaction. */
    suspend fun parse(text: String, capturedAt: Long): ParsedExpense?
}

