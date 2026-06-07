package com.jizhang.smartledger.domain.recognition

/** Extracts text from user-selected images while hiding OCR provider details. */
interface OcrEngine {
    /** Recognizes text in the given image uri and returns normalized plain text. */
    suspend fun recognize(imageUri: String): String
}
