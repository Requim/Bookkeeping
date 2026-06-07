package com.jizhang.smartledger.data.duplicate

import com.jizhang.smartledger.domain.model.CaptureSource
import com.jizhang.smartledger.domain.model.Money
import com.jizhang.smartledger.domain.model.RawCapture
import com.jizhang.smartledger.domain.model.TransactionType
import com.jizhang.smartledger.domain.recognition.ParsedExpense
import org.junit.Assert.assertEquals
import org.junit.Test

class ShaDuplicateDetectorTest {
    private val detector = ShaDuplicateDetector()

    @Test
    fun createsSameFingerprintWithinFiveMinuteBucket() {
        val first = detector.fingerprint(raw(), parsed(paidAt = 60_000L))
        val second = detector.fingerprint(raw(), parsed(paidAt = 120_000L))

        assertEquals(first, second)
    }

    private fun raw(): RawCapture {
        return RawCapture(
            source = CaptureSource.NOTIFICATION,
            appPackage = "com.tencent.mm",
            text = "支付 ¥18.50",
            capturedAt = 0L
        )
    }

    private fun parsed(paidAt: Long): ParsedExpense {
        return ParsedExpense(
            money = Money(1850),
            type = TransactionType.EXPENSE,
            merchant = "瑞幸咖啡",
            paidAt = paidAt,
            confidence = 0.9f,
            rawSummary = "支付 ¥18.50"
        )
    }
}

