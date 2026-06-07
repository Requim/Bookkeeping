package com.jizhang.smartledger.domain.usecase

import com.jizhang.smartledger.domain.model.Category
import com.jizhang.smartledger.domain.model.CategoryRule
import com.jizhang.smartledger.domain.model.ConfirmedDraftInput
import com.jizhang.smartledger.domain.model.Transaction
import com.jizhang.smartledger.domain.model.TransactionDraft
import com.jizhang.smartledger.domain.model.TransactionType
import com.jizhang.smartledger.domain.remote.RemoteCaptureResult
import com.jizhang.smartledger.domain.remote.RemoteLedgerGateway
import com.jizhang.smartledger.domain.remote.RemoteTodaySummary
import com.jizhang.smartledger.domain.repository.CategoryRepository
import com.jizhang.smartledger.domain.repository.LedgerReadRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Test

class RemoteConfirmDraftUseCaseTest {
    @Test
    fun sendsCategoryNameAndRefreshesReadState() = runTest {
        val gateway = FakeGateway()
        val readRepository = FakeLedgerReadRepository()
        val useCase = RemoteConfirmDraftUseCase(
            gateway = gateway,
            ledgerReadRepository = readRepository,
            categoryRepository = FakeCategoryRepository()
        )

        val transactionId = useCase(8L, confirmedInput())

        assertEquals("txn_001", transactionId)
        assertEquals("餐饮", gateway.confirmedCategory)
        assertEquals(1, readRepository.refreshCount)
    }

    private fun confirmedInput(): ConfirmedDraftInput {
        return ConfirmedDraftInput(
            amountCents = 1850,
            type = TransactionType.EXPENSE,
            merchant = "瑞幸咖啡",
            categoryId = 2L,
            paidAt = 1000L
        )
    }

    private class FakeGateway : RemoteLedgerGateway {
        var confirmedCategory = ""

        override suspend fun uploadCapture(capture: com.jizhang.smartledger.domain.model.RawCapture) =
            RemoteCaptureResult.Ignored

        override suspend fun pendingDrafts(): List<TransactionDraft> = emptyList()
        override suspend fun recentTransactions(limit: Int): List<Transaction> = emptyList()
        override suspend fun todaySummary() = RemoteTodaySummary(0, "CNY", 0)

        override suspend fun confirmDraft(
            draftId: Long,
            input: ConfirmedDraftInput,
            category: String
        ): String {
            confirmedCategory = category
            return "txn_001"
        }

        override suspend fun ignoreDraft(draftId: Long) = Unit
    }

    private class FakeCategoryRepository : CategoryRepository {
        override fun observeCategories(): Flow<List<Category>> = flowOf(categories())
        override suspend fun categories(): List<Category> = categories()
        override suspend fun rules(): List<CategoryRule> = emptyList()
        override suspend fun ensureDefaults() = Unit

        private fun categories(): List<Category> {
            return listOf(Category(2L, "餐饮", "#F97316", "restaurant", 0))
        }
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
