package com.jizhang.smartledger.data.local.entity

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey

/** Room row for a keyword-to-category classification rule. */
@Entity(
    tableName = "category_rules",
    foreignKeys = [
        ForeignKey(
            entity = CategoryEntity::class,
            parentColumns = ["id"],
            childColumns = ["categoryId"],
            onDelete = ForeignKey.CASCADE
        )
    ],
    indices = [Index("categoryId"), Index(value = ["keyword"], unique = true)]
)
/** Room row for a keyword-to-category classification rule. */
data class CategoryRuleEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val keyword: String,
    val categoryId: Long,
    val priority: Int
)
