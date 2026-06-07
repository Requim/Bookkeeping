package com.jizhang.smartledger.domain.usecase

import com.jizhang.smartledger.domain.model.ConfirmedDraftInput
import com.jizhang.smartledger.domain.remote.RemoteLedgerGateway
import com.jizhang.smartledger.domain.repository.CategoryRepository
import com.jizhang.smartledger.domain.repository.LedgerReadRepository

/** Confirms backend drafts from the thin Android client. */
class RemoteConfirmDraftUseCase(
    private val gateway: RemoteLedgerGateway,
    private val ledgerReadRepository: LedgerReadRepository,
    private val categoryRepository: CategoryRepository
) {
    /** Confirms a draft remotely and returns the backend transaction id. */
    suspend operator fun invoke(draftId: Long, input: ConfirmedDraftInput): String {
        val transactionId = gateway.confirmDraft(draftId, input, categoryName(input.categoryId))
        ledgerReadRepository.refresh()
        return transactionId
    }

    private suspend fun categoryName(categoryId: Long): String {
        categoryRepository.ensureDefaults()
        return categoryRepository.categories()
            .firstOrNull { it.id == categoryId }
            ?.name ?: DEFAULT_CATEGORY
    }

    private companion object {
        const val DEFAULT_CATEGORY = "其他"
    }
}
