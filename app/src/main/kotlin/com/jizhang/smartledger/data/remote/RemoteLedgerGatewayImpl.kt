package com.jizhang.smartledger.data.remote

import com.jizhang.smartledger.data.remote.api.LedgerApiClient
import com.jizhang.smartledger.data.remote.mapper.toCaptureRequestDto
import com.jizhang.smartledger.data.remote.mapper.toConfirmRequestDto
import com.jizhang.smartledger.data.remote.mapper.toDomain
import com.jizhang.smartledger.data.remote.mapper.toDraftOrNull
import com.jizhang.smartledger.domain.model.ConfirmedDraftInput
import com.jizhang.smartledger.domain.model.RawCapture
import com.jizhang.smartledger.domain.model.Transaction
import com.jizhang.smartledger.domain.model.TransactionDraft
import com.jizhang.smartledger.domain.remote.RemoteCaptureResult
import com.jizhang.smartledger.domain.remote.RemoteLedgerGateway
import com.jizhang.smartledger.domain.remote.RemoteTodaySummary

/** FastAPI-backed implementation of the remote ledger gateway. */
class RemoteLedgerGatewayImpl(
    private val apiClient: LedgerApiClient,
    private val idMapper: RemoteIdMapper = RemoteIdMapper()
) : RemoteLedgerGateway {
    override suspend fun uploadCapture(capture: RawCapture): RemoteCaptureResult {
        val response = apiClient.uploadCapture(capture.toCaptureRequestDto())
        val draft = response.toDraftOrNull(capture.source, idMapper)
        return when {
            response.status == "DUPLICATE" -> RemoteCaptureResult.Duplicate
            response.status == "IGNORED" -> RemoteCaptureResult.Ignored
            draft != null -> RemoteCaptureResult.Created(draft)
            else -> RemoteCaptureResult.Ignored
        }
    }

    override suspend fun pendingDrafts(): List<TransactionDraft> {
        return apiClient.pendingDrafts().items.map { it.toDomain(idMapper) }
    }

    override suspend fun recentTransactions(limit: Int): List<Transaction> {
        return apiClient.transactions(limit).items.map { it.toDomain(idMapper) }
    }

    override suspend fun todaySummary(): RemoteTodaySummary {
        return apiClient.todaySummary().toDomain()
    }

    override suspend fun confirmDraft(
        draftId: Long,
        input: ConfirmedDraftInput,
        category: String
    ): String {
        val response = apiClient.confirmDraft(
            idMapper.remoteId(draftId),
            input.toConfirmRequestDto(category)
        )
        return response.transactionId
    }

    override suspend fun ignoreDraft(draftId: Long) {
        apiClient.ignoreDraft(idMapper.remoteId(draftId))
    }
}
