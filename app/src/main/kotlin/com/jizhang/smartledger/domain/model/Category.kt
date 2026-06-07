package com.jizhang.smartledger.domain.model

/** User-visible spending category used for summaries and filtering. */
data class Category(
    val id: Long = 0,
    val name: String,
    val color: String,
    val icon: String,
    val sortOrder: Int
)

/** Keyword rule that maps merchant text to a category. */
data class CategoryRule(
    val id: Long = 0,
    val keyword: String,
    val categoryId: Long,
    val priority: Int
)

