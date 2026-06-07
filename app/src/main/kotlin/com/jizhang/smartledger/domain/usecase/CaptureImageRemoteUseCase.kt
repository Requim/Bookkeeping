package com.jizhang.smartledger.domain.usecase

import com.jizhang.smartledger.domain.model.CaptureSource
import com.jizhang.smartledger.domain.model.RawCapture
import com.jizhang.smartledger.domain.recognition.OcrEngine
import com.jizhang.smartledger.domain.remote.RemoteCaptureResult
import com.jizhang.smartledger.domain.time.Clock

/** OCRs a selected image and uploads the recognized text to FastAPI. */
class CaptureImageRemoteUseCase(
    private val ocrEngine: OcrEngine,
    private val remoteCaptureRaw: RemoteCaptureRawUseCase,
    private val clock: Clock
) {
    /** Recognizes the image locally and sends the text to the backend. */
    suspend operator fun invoke(imageUri: String): RemoteCaptureResult {
        val text = ocrEngine.recognize(imageUri)
        val capture = RawCapture(
            source = CaptureSource.IMAGE_OCR,
            text = text,
            imageUri = imageUri,
            capturedAt = clock.nowMillis()
        )
        return remoteCaptureRaw(capture)
    }
}
