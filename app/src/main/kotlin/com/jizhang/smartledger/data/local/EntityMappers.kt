package com.jizhang.smartledger.data.local

import com.jizhang.smartledger.data.local.entity.CategoryEntity
import com.jizhang.smartledger.data.local.entity.CategoryRuleEntity
import com.jizhang.smartledger.data.local.entity.RawCaptureEntity
import com.jizhang.smartledger.data.local.entity.TransactionDraftEntity
import com.jizhang.smartledger.data.local.entity.TransactionEntity
import com.jizhang.smartledger.domain.model.CaptureSource
import com.jizhang.smartledger.domain.model.Category
import com.jizhang.smartledger.domain.model.CategoryRule
import com.jizhang.smartledger.domain.model.DraftStatus
import com.jizhang.smartledger.domain.model.Money
import com.jizhang.smartledger.domain.model.RawCapture
import com.jizhang.smartledger.domain.model.Transaction
import com.jizhang.smartledger.domain.model.TransactionDraft
import com.jizhang.smartledger.domain.model.TransactionType

/** Maps database rows to domain category models. */
fun CategoryEntity.toDomain(): Category {
    return Category(id, name, color, icon, sortOrder)
}

/** Maps domain category models to database rows. */
fun Category.toEntity(): CategoryEntity {
    return CategoryEntity(id, name, color, icon, sortOrder)
}

/** Maps database rows to domain classification rules. */
fun CategoryRuleEntity.toDomain(): CategoryRule {
    return CategoryRule(id, keyword, categoryId, priority)
}

/** Maps domain classification rules to database rows. */
fun CategoryRule.toEntity(): CategoryRuleEntity {
    return CategoryRuleEntity(id, keyword, categoryId, priority)
}

/** Maps database rows to raw capture domain models. */
fun RawCaptureEntity.toDomain(): RawCapture {
    return RawCapture(
        id = id,
        source = CaptureSource.valueOf(source),
        appPackage = appPackage,
        title = title,
        text = text,
        imageUri = imageUri,
        capturedAt = capturedAt,
        processedAt = processedAt
    )
}

/** Maps raw capture domain models to database rows. */
fun RawCapture.toEntity(): RawCaptureEntity {
    return RawCaptureEntity(
        id = id,
        source = source.name,
        appPackage = appPackage,
        title = title,
        text = text,
        imageUri = imageUri,
        capturedAt = capturedAt,
        processedAt = processedAt
    )
}

/** Maps database rows to pending draft domain models. */
fun TransactionDraftEntity.toDomain(): TransactionDraft {
    return TransactionDraft(
        id = id,
        money = Money(amountCents, currency),
        type = TransactionType.valueOf(type),
        merchant = merchant,
        categoryId = categoryId,
        paidAt = paidAt,
        source = CaptureSource.valueOf(source),
        confidence = confidence,
        fingerprint = fingerprint,
        rawCaptureId = rawCaptureId,
        rawSummary = rawSummary,
        status = DraftStatus.valueOf(status),
        createdAt = createdAt
    )
}

/** Maps pending draft domain models to database rows. */
fun TransactionDraft.toEntity(): TransactionDraftEntity {
    return TransactionDraftEntity(
        id = id,
        amountCents = money.amountCents,
        currency = money.currency,
        type = type.name,
        merchant = merchant,
        categoryId = categoryId,
        paidAt = paidAt,
        source = source.name,
        confidence = confidence,
        fingerprint = fingerprint,
        rawCaptureId = rawCaptureId,
        rawSummary = rawSummary,
        status = status.name,
        createdAt = createdAt
    )
}

/** Maps database rows to confirmed transaction domain models. */
fun TransactionEntity.toDomain(): Transaction {
    return Transaction(
        id = id,
        money = Money(amountCents, currency),
        type = TransactionType.valueOf(type),
        merchant = merchant,
        categoryId = categoryId,
        paidAt = paidAt,
        source = CaptureSource.valueOf(source),
        rawCaptureId = rawCaptureId,
        note = note,
        createdAt = createdAt,
        updatedAt = updatedAt
    )
}

/** Maps confirmed transaction domain models to database rows. */
fun Transaction.toEntity(): TransactionEntity {
    return TransactionEntity(
        id = id,
        amountCents = money.amountCents,
        currency = money.currency,
        type = type.name,
        merchant = merchant,
        categoryId = categoryId,
        paidAt = paidAt,
        source = source.name,
        rawCaptureId = rawCaptureId,
        note = note,
        createdAt = createdAt,
        updatedAt = updatedAt
    )
}

