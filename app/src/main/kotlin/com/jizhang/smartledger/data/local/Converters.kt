package com.jizhang.smartledger.data.local

import androidx.room.TypeConverter

/** Room converters for simple collections stored in local settings tables if needed later. */
class Converters {
    /** Converts a string set into a stable comma-delimited value. */
    @TypeConverter
    fun fromStringSet(value: Set<String>?): String {
        return value.orEmpty().sorted().joinToString(",")
    }

    /** Converts a comma-delimited value into a string set. */
    @TypeConverter
    fun toStringSet(value: String?): Set<String> {
        return value?.split(",")?.filter { it.isNotBlank() }?.toSet().orEmpty()
    }
}

