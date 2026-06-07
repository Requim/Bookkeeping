package com.jizhang.smartledger.data.duplicate

import com.jizhang.smartledger.domain.model.RawCapture
import com.jizhang.smartledger.domain.model.TransactionDraft
import com.jizhang.smartledger.domain.recognition.DuplicateDetector
import com.jizhang.smartledger.domain.recognition.ParsedExpense
import java.security.MessageDigest
import kotlin.math.floor

/** Duplicate detector based on source, merchant, amount, and five-minute time buckets. */
class ShaDuplicateDetector : DuplicateDetector {
    override fun fingerprint(rawCapture: RawCapture, draft: ParsedExpense): String {
        val bucket = fiveMinuteBucket(draft.paidAt)
        val sourceKey = rawCapture.appPackage ?: rawCapture.source.name
        val base = listOf(sourceKey, draft.money.amountCents, draft.merchant, bucket)
            .joinToString("|")
            .lowercase()
        return sha256(base)
    }

    override fun isDuplicate(existing: TransactionDraft?): Boolean {
        return existing != null
    }

    private fun fiveMinuteBucket(epochMillis: Long): Long {
        return floor(epochMillis / FIVE_MINUTES_MS.toDouble()).toLong()
    }

    private fun sha256(value: String): String {
        val digest = MessageDigest.getInstance("SHA-256").digest(value.toByteArray())
        return digest.joinToString("") { byte -> "%02x".format(byte) }
    }

    private companion object {
        const val FIVE_MINUTES_MS = 5 * 60 * 1000L
    }
}

