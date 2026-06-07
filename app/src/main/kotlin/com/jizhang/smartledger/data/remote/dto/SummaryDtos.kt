package com.jizhang.smartledger.data.remote.dto

import kotlinx.serialization.Serializable

/** Today's expense summary returned by FastAPI. */
@Serializable
data class TodaySummaryDto(
    val expenseCents: Long,
    val currency: String = "CNY",
    val pendingDraftCount: Int
)
