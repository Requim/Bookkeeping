package com.jizhang.smartledger.domain.usecase

import com.jizhang.smartledger.domain.model.CaptureSource
import com.jizhang.smartledger.domain.model.Money
import com.jizhang.smartledger.domain.model.RawCapture
import com.jizhang.smartledger.domain.model.TransactionDraft
import com.jizhang.smartledger.domain.model.TransactionType
import com.jizhang.smartledger.domain.recognition.DuplicateDetector
import com.jizhang.smartledger.domain.recognition.ExpenseClassifier
import com.jizhang.smartledger.domain.recognition.ExpenseParser
import com.jizhang.smartledger.domain.recognition.ParsedExpense
import com.jizhang.smartledger.domain.repository.CaptureRepository
import com.jizhang.smartledger.domain.repository.DraftRepository
import com.jizhang.smartledger.domain.model.DraftStatus
import com.jizhang.smartledger.domain.time.Clock
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertTrue
import org.junit.Test

class ProcessRawCaptureUseCaseTest {
    @Test
    fun createsPendingDraftFromRawCapture() = runTest {
        val draftRepository = FakeDraftRepository()
        val useCase = ProcessRawCaptureUseCase(
            parser = FakeParser(),
            classifier = FakeClassifier(),
            duplicateDetector = FakeDuplicateDetector(),
            draftRepository = draftRepository,
            captureRepository = FakeCaptureRepository(),
            clock = FakeClock()
        )

        val result = useCase(raw())

        assertTrue(result is ProcessRawCaptureResult.Created)
        assertTrue(draftRepository.saved?.merchant == "瑞幸咖啡")
    }

    private fun raw(): RawCapture {
        return RawCapture(source = CaptureSource.NOTIFICATION, text = "支付", capturedAt = 1000L)
    }

    private class FakeParser : ExpenseParser {
        override suspend fun parse(text: String, capturedAt: Long): ParsedExpense {
            return ParsedExpense(Money(1800), TransactionType.EXPENSE, "瑞幸咖啡", capturedAt, 0.9f, text)
        }
    }

    private class FakeClassifier : ExpenseClassifier {
        override suspend fun classify(merchant: String, rawText: String) = 1L
    }

    private class FakeDuplicateDetector : DuplicateDetector {
        override fun fingerprint(rawCapture: RawCapture, draft: ParsedExpense) = "fingerprint"
        override fun isDuplicate(existing: TransactionDraft?) = false
    }

    private class FakeDraftRepository : DraftRepository {
        var saved: TransactionDraft? = null
        override suspend fun save(draft: TransactionDraft): Long {
            saved = draft
            return 1L
        }
        override suspend fun findByFingerprint(fingerprint: String) = null
        override suspend fun findById(id: Long) = saved
        override suspend fun updateStatus(id: Long, status: DraftStatus) = Unit
        override fun observePending(): Flow<List<TransactionDraft>> = flowOf(emptyList())
    }

    private class FakeCaptureRepository : CaptureRepository {
        override suspend fun save(capture: RawCapture) = 1L
        override suspend fun markProcessed(id: Long, processedAt: Long) = Unit
        override fun recent(limit: Int): Flow<List<RawCapture>> = flowOf(emptyList())
    }

    private class FakeClock : Clock {
        override fun nowMillis() = 2000L
        override fun startOfTodayMillis() = 0L
        override fun startOfTomorrowMillis() = 86_400_000L
    }
}
