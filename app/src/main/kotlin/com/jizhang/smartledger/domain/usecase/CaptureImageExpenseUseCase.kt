package com.jizhang.smartledger.domain.usecase

import com.jizhang.smartledger.domain.model.CaptureSource
import com.jizhang.smartledger.domain.model.RawCapture
import com.jizhang.smartledger.domain.recognition.OcrEngine
import com.jizhang.smartledger.domain.repository.CaptureRepository
import com.jizhang.smartledger.domain.time.Clock

/** Converts a user-selected image into raw capture text and a pending draft. */
class CaptureImageExpenseUseCase(
    private val ocrEngine: OcrEngine,
    private val captureRepository: CaptureRepository,
    private val processRawCapture: ProcessRawCaptureUseCase,
    private val clock: Clock
) {
    /** Runs OCR for the image and processes the recognized text as payment evidence. */
    suspend operator fun invoke(imageUri: String): ProcessRawCaptureResult {
        val text = ocrEngine.recognize(imageUri)
        val capture = RawCapture(
            source = CaptureSource.IMAGE_OCR,
            text = text,
            imageUri = imageUri,
            capturedAt = clock.nowMillis()
        )
        val id = captureRepository.save(capture)
        return processRawCapture(capture.copy(id = id))
    }
}

