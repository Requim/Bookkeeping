package com.jizhang.smartledger.domain.usecase

import com.jizhang.smartledger.domain.model.DraftStatus
import com.jizhang.smartledger.domain.repository.DraftRepository

/** Marks a parsed draft as ignored so it will not appear in review. */
class IgnoreDraftUseCase(
    private val draftRepository: DraftRepository
) {
    /** Ignores the draft with the provided identifier. */
    suspend operator fun invoke(draftId: Long) {
        draftRepository.updateStatus(draftId, DraftStatus.IGNORED)
    }
}

