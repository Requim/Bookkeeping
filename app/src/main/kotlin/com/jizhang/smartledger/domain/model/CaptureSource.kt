package com.jizhang.smartledger.domain.model

/** Describes where raw payment evidence came from. */
enum class CaptureSource {
    NOTIFICATION,
    IMAGE_OCR,
    MANUAL_TEXT,
    SCREEN_CAPTURE,
    ACCESSIBILITY
}
