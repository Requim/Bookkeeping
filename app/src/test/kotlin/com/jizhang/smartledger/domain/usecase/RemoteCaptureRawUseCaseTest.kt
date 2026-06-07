package com.jizhang.smartledger.domain.usecase

import com.jizhang.smartledger.domain.model.CaptureSource
import com.jizhang.smartledger.domain.model.ConfirmedDraftInput
import com.jizhang.smartledger.domain.model.RawCapture
import com.jizhang.smartledger.domain.model.Transaction
import com.jizhang.smartledger.domain.model.TransactionDraft
import com.jizhang.smartledger.domain.remote.RemoteCaptureResult
import com.jizhang.smartledger.domain.remote.RemoteLedgerGateway
import com.jizhang.smartledger.domain.remote.RemoteTodaySummary
import com.jizhang.smartledger.domain.repository.LedgerReadRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Test

class RemoteCaptureRawUseCaseTest {
    @Test
    fun refreshesReadStateAfterUpload() = runTest {
        val readRepository = FakeLedgerReadRepository()
        val useCase = RemoteCaptureRawUseCase(FakeGateway(), readRepository)

        val result = useCase(rawCapture())

        assertEquals(RemoteCaptureResult.Ignored, result)
        assertEquals(1, readRepository.refreshCount)
    }

    private fun rawCapture(): RawCapture {
        return RawCapture(source = CaptureSource.MANUAL_TEXT, text = "支付 1 元", capturedAt = 1000L)
    }

    private class FakeGateway : RemoteLedgerGateway {
        override suspend fun uploadCapture(capture: RawCapture) = RemoteCaptureResult.Ignored
        override suspend fun pendingDrafts(): List<TransactionDraft> = emptyList()
        override suspend fun recentTransactions(limit: Int): List<Transaction> = emptyList()
        override suspend fun todaySummary() = RemoteTodaySummary(0, "CNY", 0)
        override suspend fun confirmDraft(
            draftId: Long,
            input: ConfirmedDraftInput,
            category: String
        ) = "txn_001"
        override suspend fun ignoreDraft(draftId: Long) = Unit
    }

    private class FakeLedgerReadRepository : LedgerReadRepository {
        var refreshCount = 0
        override fun observePendingDrafts(): Flow<List<TransactionDraft>> = flowOf(emptyList())
        override fun observeRecentTransactions(limit: Int): Flow<List<Transaction>> = flowOf(emptyList())
        override fun observeTodayExpenseCents(): Flow<Long> = flowOf(0L)
        override suspend fun refresh() {
            refreshCount += 1
        }
    }
}
