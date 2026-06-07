package com.jizhang.smartledger.data.remote.mapper

import com.jizhang.smartledger.data.remote.dto.CaptureRequestDto
import com.jizhang.smartledger.data.remote.dto.CaptureResponseDto
import com.jizhang.smartledger.data.remote.dto.ConfirmDraftRequestDto
import com.jizhang.smartledger.data.remote.dto.DraftDto
import com.jizhang.smartledger.data.remote.dto.TodaySummaryDto
import com.jizhang.smartledger.data.remote.dto.TransactionDto
import com.jizhang.smartledger.data.remote.RemoteIdMapper
import com.jizhang.smartledger.domain.model.CaptureSource
import com.jizhang.smartledger.domain.model.ConfirmedDraftInput
import com.jizhang.smartledger.domain.model.DraftStatus
import com.jizhang.smartledger.domain.model.Money
import com.jizhang.smartledger.domain.model.RawCapture
import com.jizhang.smartledger.domain.model.Transaction
import com.jizhang.smartledger.domain.model.TransactionDraft
import com.jizhang.smartledger.domain.model.TransactionType
import com.jizhang.smartledger.domain.remote.RemoteTodaySummary

/** Maps raw capture domain models into FastAPI request DTOs. */
fun RawCapture.toCaptureRequestDto(): CaptureRequestDto {
    return CaptureRequestDto(
        source = source.name,
        appPackage = appPackage,
        title = title,
        text = text,
        imageBase64 = null,
        capturedAt = capturedAt
    )
}

/** Maps backend capture responses into pending draft domain models when possible. */
fun CaptureResponseDto.toDraftOrNull(
    source: CaptureSource,
    idMapper: RemoteIdMapper
): TransactionDraft? {
    val id = draftId?.let { idMapper.localId(it) } ?: return null
    return TransactionDraft(
        id = id,
        money = Money(amountCents ?: return null, currency),
        type = type.toTransactionType(),
        merchant = merchant ?: "待确认商户",
        categoryId = null,
        paidAt = paidAt ?: 0L,
        source = source,
        confidence = confidence ?: 0f,
        fingerprint = draftId,
        rawCaptureId = 0L,
        rawSummary = category.orEmpty(),
        status = status.toDraftStatus(),
        createdAt = paidAt ?: 0L
    )
}

/** Maps backend draft DTOs into Android domain draft models. */
fun DraftDto.toDomain(idMapper: RemoteIdMapper): TransactionDraft {
    return TransactionDraft(
        id = idMapper.localId(draftId),
        money = Money(amountCents, currency),
        type = type.toTransactionType(),
        merchant = merchant,
        categoryId = null,
        paidAt = paidAt,
        source = CaptureSource.NOTIFICATION,
        confidence = confidence,
        fingerprint = draftId,
        rawCaptureId = 0L,
        rawSummary = category.orEmpty(),
        status = status.toDraftStatus(),
        createdAt = paidAt
    )
}

/** Maps user confirmation input into FastAPI request DTOs. */
fun ConfirmedDraftInput.toConfirmRequestDto(category: String): ConfirmDraftRequestDto {
    return ConfirmDraftRequestDto(
        amountCents = amountCents,
        currency = "CNY",
        type = type.name,
        merchant = merchant,
        category = category,
        paidAt = paidAt,
        note = note
    )
}

/** Maps backend transaction DTOs into Android domain transaction models. */
fun TransactionDto.toDomain(idMapper: RemoteIdMapper): Transaction {
    return Transaction(
        id = idMapper.localId(transactionId),
        money = Money(amountCents, currency),
        type = type.toTransactionType(),
        merchant = merchant,
        categoryId = idMapper.localId(category),
        paidAt = paidAt,
        source = CaptureSource.NOTIFICATION,
        rawCaptureId = null,
        note = note,
        createdAt = paidAt,
        updatedAt = paidAt
    )
}

/** Maps backend summary DTOs into domain remote summary models. */
fun TodaySummaryDto.toDomain(): RemoteTodaySummary {
    return RemoteTodaySummary(expenseCents, currency, pendingDraftCount)
}

private fun String?.toTransactionType(): TransactionType {
    return runCatching { TransactionType.valueOf(this ?: "EXPENSE") }
        .getOrDefault(TransactionType.EXPENSE)
}

private fun String.toDraftStatus(): DraftStatus {
    return runCatching { DraftStatus.valueOf(this) }.getOrDefault(DraftStatus.PENDING)
}
