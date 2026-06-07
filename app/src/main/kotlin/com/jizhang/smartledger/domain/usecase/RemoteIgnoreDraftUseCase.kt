package com.jizhang.smartledger.domain.usecase

import com.jizhang.smartledger.domain.remote.RemoteLedgerGateway
import com.jizhang.smartledger.domain.repository.LedgerReadRepository

/** Ignores backend drafts from the thin Android client. */
class RemoteIgnoreDraftUseCase(
    private val gateway: RemoteLedgerGateway,
    private val ledgerReadRepository: LedgerReadRepository
) {
    /** Ignores a draft remotely and refreshes the dashboard read state. */
    suspend operator fun invoke(draftId: Long) {
        gateway.ignoreDraft(draftId)
        ledgerReadRepository.refresh()
    }
}
