package com.jizhang.smartledger.data.remote.mapper

import com.jizhang.smartledger.data.remote.RemoteIdMapper
import com.jizhang.smartledger.data.remote.dto.DraftDto
import com.jizhang.smartledger.domain.model.DraftStatus
import com.jizhang.smartledger.domain.model.TransactionType
import org.junit.Assert.assertEquals
import org.junit.Test

class RemoteMappersTest {
    @Test
    fun mapsDraftDtoToDomainDraft() {
        val mapper = RemoteIdMapper()
        val draft = draftDto().toDomain(mapper)

        assertEquals(1850L, draft.money.amountCents)
        assertEquals("瑞幸咖啡", draft.merchant)
        assertEquals(TransactionType.EXPENSE, draft.type)
        assertEquals(DraftStatus.PENDING, draft.status)
    }

    @Test
    fun keepsRemoteIdAddressableByLocalId() {
        val mapper = RemoteIdMapper()
        val localId = mapper.localId("draft_001")

        assertEquals("draft_001", mapper.remoteId(localId))
    }

    private fun draftDto(): DraftDto {
        return DraftDto(
            draftId = "draft_001",
            amountCents = 1850,
            type = "EXPENSE",
            merchant = "瑞幸咖啡",
            category = "餐饮",
            paidAt = 1000L,
            confidence = 0.93f,
            status = "PENDING"
        )
    }
}
