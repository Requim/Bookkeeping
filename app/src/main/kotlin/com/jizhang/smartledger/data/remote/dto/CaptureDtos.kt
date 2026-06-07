package com.jizhang.smartledger.data.remote.dto

import kotlinx.serialization.Serializable

/** Request body sent when Android uploads raw payment evidence to FastAPI. */
@Serializable
data class CaptureRequestDto(
    val source: String,
    val appPackage: String? = null,
    val title: String? = null,
    val text: String,
    val imageBase64: String? = null,
    val capturedAt: Long
)

/** Response body returned after FastAPI processes a raw capture. */
@Serializable
data class CaptureResponseDto(
    val draftId: String? = null,
    val amountCents: Long? = null,
    val currency: String = "CNY",
    val type: String? = null,
    val merchant: String? = null,
    val category: String? = null,
    val paidAt: Long? = null,
    val confidence: Float? = null,
    val status: String
)
