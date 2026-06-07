package com.jizhang.smartledger.domain.usecase

import com.jizhang.smartledger.domain.model.ConfirmedDraftInput
import com.jizhang.smartledger.domain.model.DraftStatus
import com.jizhang.smartledger.domain.model.Money
import com.jizhang.smartledger.domain.model.Transaction
import com.jizhang.smartledger.domain.repository.DraftRepository
import com.jizhang.smartledger.domain.repository.TransactionRepository
import com.jizhang.smartledger.domain.time.Clock

/** Turns a reviewed draft into a final transaction. */
class ConfirmDraftUseCase(
    private val draftRepository: DraftRepository,
    private val transactionRepository: TransactionRepository,
    private val clock: Clock
) {
    /** Confirms a draft with user-edited values and returns the transaction id. */
    suspend operator fun invoke(draftId: Long, input: ConfirmedDraftInput): Long {
        val draft = requireNotNull(draftRepository.findById(draftId)) {
            "Draft $draftId does not exist."
        }
        val now = clock.nowMillis()
        val transactionId = transactionRepository.save(
            Transaction(
                money = Money(input.amountCents, draft.money.currency),
                type = input.type,
                merchant = input.merchant,
                categoryId = input.categoryId,
                paidAt = input.paidAt,
                source = draft.source,
                rawCaptureId = draft.rawCaptureId,
                note = input.note,
                createdAt = now,
                updatedAt = now
            )
        )
        draftRepository.updateStatus(draftId, DraftStatus.CONFIRMED)
        return transactionId
    }
}

