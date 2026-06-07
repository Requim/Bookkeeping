package com.jizhang.smartledger.domain.usecase

import com.jizhang.smartledger.domain.model.Money
import com.jizhang.smartledger.domain.model.RawCapture
import com.jizhang.smartledger.domain.model.Settings
import com.jizhang.smartledger.domain.model.TransactionDraft
import com.jizhang.smartledger.domain.model.TransactionType
import com.jizhang.smartledger.domain.model.DraftStatus
import com.jizhang.smartledger.domain.recognition.DuplicateDetector
import com.jizhang.smartledger.domain.recognition.ExpenseClassifier
import com.jizhang.smartledger.domain.recognition.ExpenseParser
import com.jizhang.smartledger.domain.recognition.ParsedExpense
import com.jizhang.smartledger.domain.repository.CaptureRepository
import com.jizhang.smartledger.domain.repository.DraftRepository
import com.jizhang.smartledger.domain.settings.SettingsRepository
import com.jizhang.smartledger.domain.time.Clock
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertTrue
import org.junit.Test

class CaptureNotificationExpenseUseCaseTest {
    @Test
    fun ignoresUnwatchedPackages() = runTest {
        val useCase = CaptureNotificationExpenseUseCase(
            captureRepository = FakeCaptureRepository(),
            processRawCapture = fakeProcessUseCase(),
            settingsRepository = FakeSettingsRepository(),
            clock = FakeClock()
        )

        val result = useCase("com.unknown", "支付", "¥1.00", 1000L)

        assertTrue(result is ProcessRawCaptureResult.Ignored)
    }

    private fun fakeProcessUseCase(): ProcessRawCaptureUseCase {
        return ProcessRawCaptureUseCase(
            parser = FakeParser(),
            classifier = FakeClassifier(),
            duplicateDetector = FakeDuplicateDetector(),
            draftRepository = FakeDraftRepository(),
            captureRepository = FakeCaptureRepository(),
            clock = FakeClock()
        )
    }

    private class FakeSettingsRepository : SettingsRepository {
        override val settings: Flow<Settings> = flowOf(
            Settings(watchedPackages = setOf("com.tencent.mm"))
        )
        override suspend fun setScreenCaptureEnabled(enabled: Boolean) = Unit
        override suspend fun setAccessibilityCaptureEnabled(enabled: Boolean) = Unit
    }

    private class FakeCaptureRepository : CaptureRepository {
        override suspend fun save(capture: RawCapture) = 1L
        override suspend fun markProcessed(id: Long, processedAt: Long) = Unit
        override fun recent(limit: Int) = flowOf(emptyList<RawCapture>())
    }

    private class FakeClock : Clock {
        override fun nowMillis() = 1000L
        override fun startOfTodayMillis() = 0L
        override fun startOfTomorrowMillis() = 86_400_000L
    }

    private class FakeParser : ExpenseParser {
        override suspend fun parse(text: String, capturedAt: Long): ParsedExpense {
            return ParsedExpense(Money(100), TransactionType.EXPENSE, "商户", capturedAt, 0.9f, text)
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
        override suspend fun save(draft: TransactionDraft) = 1L
        override suspend fun findByFingerprint(fingerprint: String) = null
        override suspend fun findById(id: Long): TransactionDraft? = null
        override suspend fun updateStatus(id: Long, status: DraftStatus) = Unit
        override fun observePending(): Flow<List<TransactionDraft>> = flowOf(emptyList())
    }
}
