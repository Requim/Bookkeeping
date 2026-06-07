package com.jizhang.smartledger.domain.usecase

import com.jizhang.smartledger.domain.model.CaptureSource
import com.jizhang.smartledger.domain.model.ConfirmedDraftInput
import com.jizhang.smartledger.domain.model.DraftStatus
import com.jizhang.smartledger.domain.model.Money
import com.jizhang.smartledger.domain.model.Transaction
import com.jizhang.smartledger.domain.model.TransactionDraft
import com.jizhang.smartledger.domain.model.TransactionType
import com.jizhang.smartledger.domain.repository.DraftRepository
import com.jizhang.smartledger.domain.repository.TransactionRepository
import com.jizhang.smartledger.domain.time.Clock
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Test

class ConfirmDraftUseCaseTest {
    @Test
    fun confirmsDraftAndSavesTransaction() = runTest {
        val drafts = FakeDraftRepository()
        val transactions = FakeTransactionRepository()
        val useCase = ConfirmDraftUseCase(drafts, transactions, FakeClock())

        useCase(1L, input())

        assertEquals(DraftStatus.CONFIRMED, drafts.status)
        assertEquals("瑞幸咖啡", transactions.saved!!.merchant)
    }

    private fun input(): ConfirmedDraftInput {
        return ConfirmedDraftInput(1850, TransactionType.EXPENSE, "瑞幸咖啡", 1L, 1000L)
    }

    private class FakeClock : Clock {
        override fun nowMillis() = 2000L
        override fun startOfTodayMillis() = 0L
        override fun startOfTomorrowMillis() = 86_400_000L
    }

    private class FakeDraftRepository : DraftRepository {
        var status = DraftStatus.PENDING

        override suspend fun save(draft: TransactionDraft) = 1L
        override suspend fun findByFingerprint(fingerprint: String) = null
        override fun observePending(): Flow<List<TransactionDraft>> = flowOf(emptyList())

        override suspend fun findById(id: Long): TransactionDraft {
            return TransactionDraft(
                id = id,
                money = Money(1850),
                type = TransactionType.EXPENSE,
                merchant = "旧商户",
                categoryId = null,
                paidAt = 1000L,
                source = CaptureSource.NOTIFICATION,
                confidence = 0.8f,
                fingerprint = "abc",
                rawCaptureId = 10L,
                rawSummary = "支付",
                createdAt = 1000L
            )
        }

        override suspend fun updateStatus(id: Long, status: DraftStatus) {
            this.status = status
        }
    }

    private class FakeTransactionRepository : TransactionRepository {
        var saved: Transaction? = null

        override suspend fun save(transaction: Transaction): Long {
            saved = transaction
            return 1L
        }

        override fun observeRecent(limit: Int): Flow<List<Transaction>> = flowOf(emptyList())

        override fun observeExpenseTotal(
            startInclusive: Long,
            endExclusive: Long
        ): Flow<Long> = flowOf(0L)
    }
}

