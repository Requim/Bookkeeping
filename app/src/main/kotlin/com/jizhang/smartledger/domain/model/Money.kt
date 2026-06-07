package com.jizhang.smartledger.domain.model

import java.math.BigDecimal
import java.math.RoundingMode

/** Money amount stored as minor units to avoid floating point drift. */
data class Money(
    val amountCents: Long,
    val currency: String = "CNY"
) {
    /** Formats the amount with two decimal places for Chinese Yuan displays. */
    fun format(): String {
        return BigDecimal(amountCents)
            .movePointLeft(2)
            .setScale(2, RoundingMode.HALF_UP)
            .toPlainString()
    }
}

