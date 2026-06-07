package com.jizhang.smartledger.domain.usecase

import com.jizhang.smartledger.domain.model.RawCapture
import com.jizhang.smartledger.domain.model.TransactionDraft
import com.jizhang.smartledger.domain.recognition.DuplicateDetector
import com.jizhang.smartledger.domain.recognition.ExpenseClassifier
import com.jizhang.smartledger.domain.recognition.ExpenseParser
import com.jizhang.smartledger.domain.recognition.ParsedExpense
import com.jizhang.smartledger.domain.repository.CaptureRepository
import com.jizhang.smartledger.domain.repository.DraftRepository
import com.jizhang.smartledger.domain.time.Clock

/** Converts raw capture text into a pending draft through parser, classifier, and dedupe. */
class ProcessRawCaptureUseCase(
    private val parser: ExpenseParser,
    private val classifier: ExpenseClassifier,
    private val duplicateDetector: DuplicateDetector,
    private val draftRepository: DraftRepository,
    private val captureRepository: CaptureRepository,
    private val clock: Clock
) {
    /** Processes raw evidence and returns whether a new draft was created. */
    suspend operator fun invoke(capture: RawCapture): ProcessRawCaptureResult {
        val parsed = parser.parse(capture.text, capture.capturedAt) ?: return markIgnored(capture)
        val fingerprint = duplicateDetector.fingerprint(capture, parsed)
        val existing = draftRepository.findByFingerprint(fingerprint)
        if (duplicateDetector.isDuplicate(existing)) {
            captureRepository.markProcessed(capture.id, clock.nowMillis())
            return ProcessRawCaptureResult.Duplicate
        }
        val draft = buildDraft(capture, parsed, fingerprint)
        val id = draftRepository.save(draft)
        captureRepository.markProcessed(capture.id, clock.nowMillis())
        return ProcessRawCaptureResult.Created(id)
    }

    private suspend fun markIgnored(capture: RawCapture): ProcessRawCaptureResult {
        captureRepository.markProcessed(capture.id, clock.nowMillis())
        return ProcessRawCaptureResult.Ignored
    }

    private suspend fun buildDraft(
        capture: RawCapture,
        parsed: ParsedExpense,
        fingerprint: String
    ): TransactionDraft {
        return TransactionDraft(
            money = parsed.money,
            type = parsed.type,
            merchant = parsed.merchant,
            categoryId = classifier.classify(parsed.merchant, capture.text),
            paidAt = parsed.paidAt,
            source = capture.source,
            confidence = parsed.confidence,
            fingerprint = fingerprint,
            rawCaptureId = capture.id,
            rawSummary = parsed.rawSummary,
            createdAt = clock.nowMillis()
        )
    }
}

/** Outcome of processing raw evidence into a draft. */
sealed interface ProcessRawCaptureResult {
    /** A new draft was created with the provided id. */
    data class Created(val draftId: Long) : ProcessRawCaptureResult

    /** Evidence matched an already known draft. */
    data object Duplicate : ProcessRawCaptureResult

    /** Evidence did not look like a supported transaction. */
    data object Ignored : ProcessRawCaptureResult
}

