package com.jizhang.smartledger.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

/** Room row for a user-visible spending category. */
@Entity(tableName = "categories")
data class CategoryEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val name: String,
    val color: String,
    val icon: String,
    val sortOrder: Int
)

