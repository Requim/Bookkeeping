package com.jizhang.smartledger.data.local.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.jizhang.smartledger.data.local.entity.CategoryEntity
import com.jizhang.smartledger.data.local.entity.CategoryRuleEntity
import kotlinx.coroutines.flow.Flow

/** Database access for categories and keyword rules. */
@Dao
interface CategoryDao {
    /** Emits all categories in UI display order. */
    @Query("SELECT * FROM categories ORDER BY sortOrder ASC")
    fun observeCategories(): Flow<List<CategoryEntity>>

    /** Reads all categories in UI display order. */
    @Query("SELECT * FROM categories ORDER BY sortOrder ASC")
    suspend fun categories(): List<CategoryEntity>

    /** Reads all category rules ordered by priority. */
    @Query("SELECT * FROM category_rules ORDER BY priority DESC")
    suspend fun rules(): List<CategoryRuleEntity>

    /** Returns the number of categories in the database. */
    @Query("SELECT COUNT(*) FROM categories")
    suspend fun countCategories(): Int

    /** Inserts categories and returns generated ids. */
    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insertCategories(categories: List<CategoryEntity>): List<Long>

    /** Inserts category rules while ignoring duplicate keywords. */
    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insertRules(rules: List<CategoryRuleEntity>)
}
