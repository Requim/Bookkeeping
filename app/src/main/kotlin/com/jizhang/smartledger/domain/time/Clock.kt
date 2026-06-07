package com.jizhang.smartledger.domain.time

/** Supplies time for use cases without coupling domain code to platform APIs. */
interface Clock {
    /** Returns current epoch milliseconds. */
    fun nowMillis(): Long

    /** Returns the start of the current local day as epoch milliseconds. */
    fun startOfTodayMillis(): Long

    /** Returns the start of the next local day as epoch milliseconds. */
    fun startOfTomorrowMillis(): Long
}

