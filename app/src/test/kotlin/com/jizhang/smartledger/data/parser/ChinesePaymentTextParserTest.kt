package com.jizhang.smartledger.data.parser

import com.jizhang.smartledger.domain.model.TransactionType
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotNull
import org.junit.Test

class ChinesePaymentTextParserTest {
    private val parser = ChinesePaymentTextParser()

    @Test
    fun parsesWechatPaymentNotification() = runTest {
        val parsed = parser.parse("微信支付 商户：瑞幸咖啡 支付成功 ¥18.50", 1000L)

        assertNotNull(parsed)
        assertEquals(1850L, parsed!!.money.amountCents)
        assertEquals("瑞幸咖啡", parsed.merchant)
        assertEquals(TransactionType.EXPENSE, parsed.type)
    }

    @Test
    fun parsesAlipayTransferAsTransferDraft() = runTest {
        val parsed = parser.parse("支付宝 向张三转账 200.00元 交易成功", 1000L)

        assertNotNull(parsed)
        assertEquals(20_000L, parsed!!.money.amountCents)
        assertEquals(TransactionType.TRANSFER, parsed.type)
    }

    @Test
    fun prefersPaymentContextAmountInOcrText() = runTest {
        val parsed = parser.parse("订单原价 29.00 优惠 5.00 实付金额 24.00元", 1000L)

        assertNotNull(parsed)
        assertEquals(2400L, parsed!!.money.amountCents)
    }
}

