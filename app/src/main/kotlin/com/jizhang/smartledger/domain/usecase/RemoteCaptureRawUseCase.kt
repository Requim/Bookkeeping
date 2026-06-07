package com.jizhang.smartledger.domain.usecase

import com.jizhang.smartledger.domain.model.RawCapture
import com.jizhang.smartledger.domain.remote.RemoteCaptureResult
import com.jizhang.smartledger.domain.remote.RemoteLedgerGateway
import com.jizhang.smartledger.domain.repository.LedgerReadRepository

/** Uploads raw capture evidence to FastAPI and refreshes ledger read state. */
class RemoteCaptureRawUseCase(
    private val gateway: RemoteLedgerGateway,
    private val ledgerReadRepository: LedgerReadRepository
) {
    /** Uploads raw evidence to the backend and returns the backend processing result. */
    suspend operator fun invoke(capture: RawCapture): RemoteCaptureResult {
        val result = gateway.uploadCapture(capture)
        ledgerReadRepository.refresh()
        return result
    }
}
