"""Temporary rule parser used until the P3 AI parsing stage replaces it."""

import re

from app.domain.models import (
    DraftStatus,
    RawCapture,
    TransactionDraft,
    TransactionType,
)
from app.domain.services import ExpenseDraftParser


class SimpleExpenseDraftParser(ExpenseDraftParser):
    """Extracts a basic expense draft from common Chinese payment text."""

    def parse(self, capture: RawCapture) -> TransactionDraft | None:
        """Return a pending draft when a payment amount is found."""
        amount_cents = self._amount_cents(capture.text)
        if amount_cents is None:
            return None
        return TransactionDraft(
            draft_id="",
            amount_cents=amount_cents,
            currency="CNY",
            transaction_type=self._transaction_type(capture.text),
            merchant=self._merchant(capture),
            category="其他",
            paid_at=capture.captured_at,
            confidence=0.6,
            status=DraftStatus.PENDING,
            raw_capture_id=capture.capture_id,
        )

    def _amount_cents(self, text: str) -> int | None:
        matches = re.findall(r"(?:¥|￥|楼)?\s*(\d+(?:\.\d{1,2})?)\s*元?", text)
        if not matches:
            return None
        amount = max(float(value) for value in matches)
        return int(round(amount * 100))

    def _merchant(self, capture: RawCapture) -> str:
        text = capture.text.replace("\n", " ")
        match = re.search(r"(?:商户|收款方|付款给)[:：]\s*([^\s，,。]+)", text)
        if match:
            return match.group(1)
        return capture.title or "待确认商户"

    def _transaction_type(self, text: str) -> TransactionType:
        if "退款" in text:
            return TransactionType.REFUND
        if "收入" in text or "收款" in text:
            return TransactionType.INCOME
        if "转账" in text:
            return TransactionType.TRANSFER
        return TransactionType.EXPENSE
