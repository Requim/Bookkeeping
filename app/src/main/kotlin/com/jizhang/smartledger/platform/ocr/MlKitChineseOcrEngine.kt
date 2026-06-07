package com.jizhang.smartledger.platform.ocr

import android.content.Context
import android.net.Uri
import com.google.mlkit.vision.common.InputImage
import com.google.mlkit.vision.text.TextRecognition
import com.google.mlkit.vision.text.chinese.ChineseTextRecognizerOptions
import com.jizhang.smartledger.domain.recognition.OcrEngine
import kotlinx.coroutines.tasks.await

/** ML Kit Chinese OCR adapter for user-selected payment screenshots. */
class MlKitChineseOcrEngine(
    private val context: Context
) : OcrEngine {
    private val recognizer = TextRecognition.getClient(
        ChineseTextRecognizerOptions.Builder().build()
    )

    override suspend fun recognize(imageUri: String): String {
        val image = InputImage.fromFilePath(context, Uri.parse(imageUri))
        return recognizer.process(image).await().text
    }
}

