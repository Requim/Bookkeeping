package com.jizhang.smartledger.domain.usecase

import com.jizhang.smartledger.domain.model.CaptureSource
import com.jizhang.smartledger.domain.model.RawCapture
import com.jizhang.smartledger.domain.remote.RemoteCaptureResult
import com.jizhang.smartledger.domain.time.Clock

/** Uploads manually entered payment text to FastAPI. */
class CaptureManualTextRemoteUseCase(
    private val remoteCaptureRaw: RemoteCaptureRawUseCase,
    private val clock: Clock
) {
    /** Sends manual text through the backend capture pipeline. */
    suspend operator fun invoke(text: String): RemoteCaptureResult {
        val capture = RawCapture(
            source = CaptureSource.MANUAL_TEXT,
            text = text,
            capturedAt = clock.nowMillis()
        )
        return remoteCaptureRaw(capture)
    }
}
