"""Tests for the temporary P2 rule parser."""

from app.domain.models import CaptureSource, RawCapture, TransactionType
from app.infrastructure.simple_parser import SimpleExpenseDraftParser


def test_simple_parser_extracts_amount_and_merchant() -> None:
    """Parse a common payment text into a draft."""
    parser = SimpleExpenseDraftParser()
    capture = RawCapture(
        capture_id="capture_001",
        source=CaptureSource.NOTIFICATION,
        title="微信支付",
        text="商户：瑞幸咖啡 支付成功 ¥18.50",
        captured_at=1780819200000,
    )

    draft = parser.parse(capture)

    assert draft is not None
    assert draft.amount_cents == 1850
    assert draft.merchant == "瑞幸咖啡"
    assert draft.transaction_type == TransactionType.EXPENSE
