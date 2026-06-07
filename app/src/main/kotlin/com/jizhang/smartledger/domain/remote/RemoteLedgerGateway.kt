package com.jizhang.smartledger.domain.remote

import com.jizhang.smartledger.domain.model.ConfirmedDraftInput
import com.jizhang.smartledger.domain.model.RawCapture
import com.jizhang.smartledger.domain.model.Transaction
import com.jizhang.smartledger.domain.model.TransactionDraft

/** Remote ledger boundary used by the thin Android client to call FastAPI. */
interface RemoteLedgerGateway {
    /** Uploads raw payment evidence and returns the backend-created draft. */
    suspend fun uploadCapture(capture: RawCapture): RemoteCaptureResult

    /** Loads pending drafts from the backend. */
    suspend fun pendingDrafts(): List<TransactionDraft>

    /** Loads recent confirmed transactions from the backend. */
    suspend fun recentTransactions(limit: Int): List<Transaction>

    /** Loads today's expense summary from the backend. */
    suspend fun todaySummary(): RemoteTodaySummary

    /** Confirms a backend draft and returns the confirmed transaction id. */
    suspend fun confirmDraft(draftId: Long, input: ConfirmedDraftInput, category: String): String

    /** Ignores a backend draft. */
    suspend fun ignoreDraft(draftId: Long)
}

/** Result returned after uploading raw evidence to the backend. */
sealed interface RemoteCaptureResult {
    /** Backend created or returned a pending draft. */
    data class Created(val draft: TransactionDraft) : RemoteCaptureResult

    /** Backend treated the uploaded capture as a duplicate. */
    data object Duplicate : RemoteCaptureResult

    /** Backend ignored the capture because it did not look like a transaction. */
    data object Ignored : RemoteCaptureResult
}

/** Today's summary returned by the backend. */
data class RemoteTodaySummary(
    val expenseCents: Long,
    val currency: String,
    val pendingDraftCount: Int
)
