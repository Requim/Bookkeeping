package com.jizhang.smartledger.data.classifier

import com.jizhang.smartledger.domain.recognition.ExpenseClassifier
import com.jizhang.smartledger.domain.repository.CategoryRepository

/** Local keyword classifier for merchant and payment text. */
class RuleBasedExpenseClassifier(
    private val categoryRepository: CategoryRepository
) : ExpenseClassifier {
    override suspend fun classify(merchant: String, rawText: String): Long? {
        categoryRepository.ensureDefaults()
        val text = "$merchant\n$rawText".lowercase()
        return categoryRepository.rules()
            .firstOrNull { rule -> text.contains(rule.keyword.lowercase()) }
            ?.categoryId
            ?: fallbackOtherCategory()
    }

    private suspend fun fallbackOtherCategory(): Long? {
        return categoryRepository.categories()
            .firstOrNull { it.name == "其他" }
            ?.id
    }
}
