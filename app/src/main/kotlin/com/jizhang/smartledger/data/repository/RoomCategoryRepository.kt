package com.jizhang.smartledger.data.repository

import com.jizhang.smartledger.data.local.dao.CategoryDao
import com.jizhang.smartledger.data.local.CategorySeed
import com.jizhang.smartledger.data.local.defaultCategorySeeds
import com.jizhang.smartledger.data.local.entity.CategoryRuleEntity
import com.jizhang.smartledger.data.local.toDomain
import com.jizhang.smartledger.domain.model.Category
import com.jizhang.smartledger.domain.model.CategoryRule
import com.jizhang.smartledger.domain.repository.CategoryRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

/** Room-backed category and classification rule repository. */
class RoomCategoryRepository(
    private val dao: CategoryDao
) : CategoryRepository {
    override fun observeCategories(): Flow<List<Category>> {
        return dao.observeCategories().map { rows -> rows.map { it.toDomain() } }
    }

    override suspend fun categories(): List<Category> {
        return dao.categories().map { it.toDomain() }
    }

    override suspend fun rules(): List<CategoryRule> {
        return dao.rules().map { it.toDomain() }
    }

    override suspend fun ensureDefaults() {
        if (dao.countCategories() > 0) {
            return
        }
        val defaults = defaultCategorySeeds()
        val ids = dao.insertCategories(defaults.map { it.category })
        dao.insertRules(buildRules(defaults, ids))
    }

    private fun buildRules(
        defaults: List<CategorySeed>,
        ids: List<Long>
    ): List<CategoryRuleEntity> {
        return defaults.flatMapIndexed { index, seed ->
            seed.rules.map { keyword -> rule(seed, keyword, ids[index]) }
        }
    }

    private fun rule(
        seed: CategorySeed,
        keyword: String,
        categoryId: Long
    ): CategoryRuleEntity {
        return CategoryRuleEntity(
            keyword = keyword,
            categoryId = categoryId,
            priority = seed.category.sortOrder
        )
    }
}
