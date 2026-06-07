package com.jizhang.smartledger.domain.model

/** Lifecycle state for a transaction candidate before final bookkeeping. */
enum class DraftStatus {
    PENDING,
    CONFIRMED,
    IGNORED
}

