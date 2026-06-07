package com.jizhang.smartledger.platform.time

import com.jizhang.smartledger.domain.time.Clock
import java.time.LocalDate
import java.time.ZoneId

/** System clock implementation for Android runtime use. */
class SystemClock(
    private val zoneId: ZoneId = ZoneId.systemDefault()
) : Clock {
    override fun nowMillis(): Long {
        return System.currentTimeMillis()
    }

    override fun startOfTodayMillis(): Long {
        return LocalDate.now(zoneId).atStartOfDay(zoneId).toInstant().toEpochMilli()
    }

    override fun startOfTomorrowMillis(): Long {
        return LocalDate.now(zoneId).plusDays(1).atStartOfDay(zoneId).toInstant().toEpochMilli()
    }
}

