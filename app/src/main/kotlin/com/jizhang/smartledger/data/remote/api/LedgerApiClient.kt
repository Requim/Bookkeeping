package com.jizhang.smartledger.data.remote.api

import com.jizhang.smartledger.data.remote.dto.CaptureRequestDto
import com.jizhang.smartledger.data.remote.dto.CaptureResponseDto
import com.jizhang.smartledger.data.remote.dto.ConfirmDraftRequestDto
import com.jizhang.smartledger.data.remote.dto.ConfirmDraftResponseDto
import com.jizhang.smartledger.data.remote.dto.DraftListResponseDto
import com.jizhang.smartledger.data.remote.dto.IgnoreDraftResponseDto
import com.jizhang.smartledger.data.remote.dto.TodaySummaryDto
import com.jizhang.smartledger.data.remote.dto.TransactionListResponseDto
import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.request.get
import io.ktor.client.request.patch
import io.ktor.client.request.post
import io.ktor.client.request.setBody
import io.ktor.http.ContentType
import io.ktor.http.contentType

/** Thin Ktor client for the FastAPI ledger backend. */
class LedgerApiClient(
    private val httpClient: HttpClient,
    private val baseUrlProvider: () -> String
) {
    /** Uploads raw capture evidence to the backend. */
    suspend fun uploadCapture(request: CaptureRequestDto): CaptureResponseDto {
        return httpClient.post(url("/api/captures")) {
            contentType(ContentType.Application.Json)
            setBody(request)
        }.body()
    }

    /** Loads pending backend drafts. */
    suspend fun pendingDrafts(): DraftListResponseDto {
        return httpClient.get(url("/api/drafts?status=PENDING")).body()
    }

    /** Confirms a backend draft. */
    suspend fun confirmDraft(
        draftId: String,
        request: ConfirmDraftRequestDto
    ): ConfirmDraftResponseDto {
        return httpClient.patch(url("/api/drafts/$draftId/confirm")) {
            contentType(ContentType.Application.Json)
            setBody(request)
        }.body()
    }

    /** Ignores a backend draft. */
    suspend fun ignoreDraft(draftId: String): IgnoreDraftResponseDto {
        return httpClient.patch(url("/api/drafts/$draftId/ignore")).body()
    }

    /** Loads recent confirmed transactions. */
    suspend fun transactions(limit: Int): TransactionListResponseDto {
        return httpClient.get(url("/api/transactions?limit=$limit")).body()
    }

    /** Loads today's backend summary. */
    suspend fun todaySummary(): TodaySummaryDto {
        return httpClient.get(url("/api/summary/today")).body()
    }

    private fun url(path: String): String {
        return baseUrlProvider().trimEnd('/') + path
    }
}
