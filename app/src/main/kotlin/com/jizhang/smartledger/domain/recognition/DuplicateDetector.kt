package com.jizhang.smartledger.domain.recognition

import com.jizhang.smartledger.domain.model.RawCapture
import com.jizhang.smartledger.domain.model.TransactionDraft

/** Creates stable keys and checks whether parsed candidates are already known. */
interface DuplicateDetector {
    /** Builds a stable fingerprint from raw evidence and parsed transaction data. */
    fun fingerprint(rawCapture: RawCapture, draft: ParsedExpense): String

    /** Returns true when an existing draft should block a new candidate. */
    fun isDuplicate(existing: TransactionDraft?): Boolean
}

