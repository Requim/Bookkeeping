package com.jizhang.smartledger.domain.model

/** Original evidence captured from notification, OCR, or an experimental screen source. */
data class RawCapture(
    val id: Long = 0,
    val source: CaptureSource,
    val appPackage: String? = null,
    val title: String? = null,
    val text: String,
    val imageUri: String? = null,
    val capturedAt: Long,
    val processedAt: Long? = null
)

