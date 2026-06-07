package com.jizhang.smartledger.domain.usecase

import com.jizhang.smartledger.domain.model.CaptureSource
import com.jizhang.smartledger.domain.model.RawCapture
import com.jizhang.smartledger.domain.repository.CaptureRepository
import com.jizhang.smartledger.domain.time.Clock

/** Captures manually entered payment text for local testing and fallback entry. */
class CaptureManualTextUseCase(
    private val captureRepository: CaptureRepository,
    private val processRawCapture: ProcessRawCaptureUseCase,
    private val clock: Clock
) {
    /** Stores manual text and processes it through the same parsing pipeline. */
    suspend operator fun invoke(text: String): ProcessRawCaptureResult {
        val capture = RawCapture(
            source = CaptureSource.MANUAL_TEXT,
            text = text,
            capturedAt = clock.nowMillis()
        )
        val id = captureRepository.save(capture)
        return processRawCapture(capture.copy(id = id))
    }
}
