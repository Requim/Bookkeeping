package com.jizhang.smartledger.domain.repository

import com.jizhang.smartledger.domain.model.Category
import com.jizhang.smartledger.domain.model.CategoryRule
import kotlinx.coroutines.flow.Flow

/** Provides category metadata and merchant classification rules. */
interface CategoryRepository {
    /** Emits all categories in display order. */
    fun observeCategories(): Flow<List<Category>>

    /** Reads all categories once for use cases that need a snapshot. */
    suspend fun categories(): List<Category>

    /** Reads all category keyword rules sorted by priority. */
    suspend fun rules(): List<CategoryRule>

    /** Inserts default categories and rules when the database is empty. */
    suspend fun ensureDefaults()
}

