package com.jizhang.smartledger.data.classifier

import com.jizhang.smartledger.domain.model.Category
import com.jizhang.smartledger.domain.model.CategoryRule
import com.jizhang.smartledger.domain.repository.CategoryRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Test

class RuleBasedExpenseClassifierTest {
    @Test
    fun matchesHighestPriorityKeywordRule() = runTest {
        val repository = FakeCategoryRepository()
        val classifier = RuleBasedExpenseClassifier(repository)

        val categoryId = classifier.classify("瑞幸咖啡", "微信支付成功")

        assertEquals(1L, categoryId)
    }

    private class FakeCategoryRepository : CategoryRepository {
        override fun observeCategories(): Flow<List<Category>> {
            return flowOf(categories)
        }

        override suspend fun categories(): List<Category> {
            return categories
        }

        override suspend fun rules(): List<CategoryRule> {
            return listOf(CategoryRule(keyword = "咖啡", categoryId = 1L, priority = 10))
        }

        override suspend fun ensureDefaults() = Unit

        private val categories = listOf(
            Category(id = 1L, name = "餐饮", color = "#fff", icon = "restaurant", sortOrder = 1),
            Category(id = 2L, name = "其他", color = "#000", icon = "more", sortOrder = 2)
        )
    }
}

