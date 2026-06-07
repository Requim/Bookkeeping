package com.jizhang.smartledger.data.parser

import com.jizhang.smartledger.domain.model.Money
import com.jizhang.smartledger.domain.model.TransactionType
import com.jizhang.smartledger.domain.recognition.ExpenseParser
import com.jizhang.smartledger.domain.recognition.ParsedExpense
import java.math.BigDecimal
import java.math.RoundingMode

/** Rule-based parser for common Chinese payment notifications and OCR text. */
class ChinesePaymentTextParser : ExpenseParser {
    override suspend fun parse(text: String, capturedAt: Long): ParsedExpense? {
        val normalized = normalize(text)
        if (!looksFinancial(normalized)) {
            return null
        }
        val money = extractMoney(normalized) ?: return null
        val type = detectType(normalized)
        return ParsedExpense(
            money = money,
            type = type,
            merchant = extractMerchant(normalized),
            paidAt = capturedAt,
            confidence = confidence(normalized, money),
            rawSummary = normalized.take(MAX_SUMMARY_LENGTH)
        )
    }

    private fun normalize(text: String): String {
        return text
            .replace('\u00A0', ' ')
            .lines()
            .map { it.trim() }
            .filter { it.isNotBlank() }
            .joinToString(" ")
    }

    private fun looksFinancial(text: String): Boolean {
        val hasMoneyWord = MONEY_WORDS.any { text.contains(it) }
        val hasAmount = AMOUNT_REGEX.containsMatchIn(text)
        return hasMoneyWord && hasAmount
    }

    private fun extractMoney(text: String): Money? {
        val directAmount = PAY_AMOUNT_REGEX.find(text)?.groupValues?.get(1)
        if (directAmount != null) {
            return Money(toCents(directAmount))
        }
        val matches = AMOUNT_REGEX.findAll(text).toList()
        val preferred = matches.lastOrNull { isPreferredAmountContext(text, it.range.first) }
        val amount = (preferred ?: matches.firstOrNull())?.groupValues?.get(1)
        return amount?.let { Money(toCents(it)) }
    }

    private fun isPreferredAmountContext(text: String, index: Int): Boolean {
        val start = (index - CONTEXT_WINDOW).coerceAtLeast(0)
        val end = (index + CONTEXT_WINDOW).coerceAtMost(text.length)
        val context = text.substring(start, end)
        return PAY_CONTEXT_WORDS.any { context.contains(it) }
    }

    private fun toCents(amount: String): Long {
        return BigDecimal(amount)
            .movePointRight(2)
            .setScale(0, RoundingMode.HALF_UP)
            .toLong()
    }

    private fun detectType(text: String): TransactionType {
        return when {
            REFUND_WORDS.any { text.contains(it) } -> TransactionType.REFUND
            INCOME_WORDS.any { text.contains(it) } -> TransactionType.INCOME
            TRANSFER_WORDS.any { text.contains(it) } -> TransactionType.TRANSFER
            else -> TransactionType.EXPENSE
        }
    }

    private fun extractMerchant(text: String): String {
        val explicit = MERCHANT_REGEX.find(text)?.groupValues?.get(1)?.trim()
        if (!explicit.isNullOrBlank()) {
            return cleanupMerchant(explicit)
        }
        return inferMerchantFromLines(text)
    }

    private fun inferMerchantFromLines(text: String): String {
        val candidates = text.split(" ", "，", ",")
            .map { cleanupMerchant(it) }
            .filter { it.length in 2..24 }
        return candidates.firstOrNull { isMerchantCandidate(it) } ?: "待确认商户"
    }

    private fun cleanupMerchant(value: String): String {
        return value
            .replace(AMOUNT_REGEX, "")
            .replace(TRAILING_NOISE_REGEX, "")
            .trim(' ', ':', '：', '-', '，', ',', '。')
    }

    private fun isMerchantCandidate(value: String): Boolean {
        val blocked = MONEY_WORDS + PAY_CONTEXT_WORDS + REFUND_WORDS + INCOME_WORDS
        return blocked.none { value.contains(it) }
    }

    private fun confidence(text: String, money: Money): Float {
        val hasMerchant = extractMerchant(text) != "待确认商户"
        val base = if (money.amountCents > 0) 0.55f else 0.25f
        val merchantBonus = if (hasMerchant) 0.25f else 0f
        val contextBonus = if (PAY_CONTEXT_WORDS.any { text.contains(it) }) 0.15f else 0f
        return (base + merchantBonus + contextBonus).coerceAtMost(0.95f)
    }

    private companion object {
        const val CONTEXT_WINDOW = 12
        const val MAX_SUMMARY_LENGTH = 120
        val AMOUNT_REGEX = Regex("""(?:￥|¥|CNY|人民币)?\s*([0-9]+(?:\.[0-9]{1,2})?)\s*元?""")
        val PAY_AMOUNT_REGEX = Regex("""(?:实付金额|交易金额|支付金额|付款金额|消费金额|实付|应付|金额)\s*[:：]?\s*(?:￥|¥|CNY|人民币)?\s*([0-9]+(?:\.[0-9]{1,2})?)\s*元?""")
        val MERCHANT_REGEX = Regex("""(?:商户|收款方|付款给|向|给)\s*[:：]?\s*([^，。,；;\s]{2,24})""")
        val TRAILING_NOISE_REGEX = Regex("""(支付|收款|付款|扣款|消费|交易|成功|通知)$""")
        val MONEY_WORDS = listOf("支付", "付款", "扣款", "消费", "交易", "收款", "退款", "转账", "金额", "实付", "到账", "入账")
        val PAY_CONTEXT_WORDS = listOf("支付", "付款", "扣款", "消费", "实付", "金额", "交易")
        val REFUND_WORDS = listOf("退款", "退回", "已退")
        val INCOME_WORDS = listOf("收款", "入账", "到账", "收入")
        val TRANSFER_WORDS = listOf("转账", "红包")
    }
}
