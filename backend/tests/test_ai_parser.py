"""Tests for the AI parser adapter and fallback behavior."""

from app.domain.models import (
    CaptureSource,
    ParsedDraftData,
    RawCapture,
    TransactionType,
)
from app.infrastructure.ai_parser import AiExpenseDraftParser
from app.infrastructure.simple_parser import SimpleExpenseDraftParser


def test_ai_parser_handles_wechat_sample() -> None:
    """Use AI structured output for a WeChat payment sample."""
    draft = _parse_with_ai("微信支付", "商户：瑞幸咖啡 支付成功 ¥18.50", "瑞幸咖啡")

    assert draft is not None
    assert draft.amount_cents == 1850
    assert draft.merchant == "瑞幸咖啡"


def test_ai_parser_handles_alipay_sample() -> None:
    """Use AI structured output for an Alipay payment sample."""
    draft = _parse_with_ai("支付宝", "付款给 地铁出行 6.00元", "地铁出行")

    assert draft is not None
    assert draft.category == "交通"
    assert draft.transaction_type == TransactionType.EXPENSE


def test_ai_parser_handles_bank_sample() -> None:
    """Use AI structured output for a bank card notification sample."""
    draft = _parse_with_ai("招商银行", "您尾号1234消费人民币88.00元", "银行卡消费")

    assert draft is not None
    assert draft.amount_cents == 8800


def test_ai_parser_falls_back_without_ai_result() -> None:
    """Use the local fallback when the AI client has no structured result."""
    parser = AiExpenseDraftParser(EmptyAiClient(), SimpleExpenseDraftParser())

    draft = parser.parse(_capture("微信支付", "商户：便利店 支付成功 ¥12.30"))

    assert draft is not None
    assert draft.amount_cents == 1230
    assert draft.merchant == "便利店"


def _parse_with_ai(title: str, text: str, merchant: str):
    parser = AiExpenseDraftParser(FakeAiClient(merchant), SimpleExpenseDraftParser())
    return parser.parse(_capture(title, text))


def _capture(title: str, text: str) -> RawCapture:
    return RawCapture(
        capture_id="capture_001",
        source=CaptureSource.NOTIFICATION,
        title=title,
        text=text,
        captured_at=1780819200000,
    )


class FakeAiClient:
    """Fake AI client that returns deterministic structured data."""

    def __init__(self, merchant: str) -> None:
        self._merchant = merchant

    def parse_capture(self, capture: RawCapture) -> ParsedDraftData | None:
        """Return parsed fields derived from the sample title."""
        return ParsedDraftData(
            amount_cents=_amount_for_title(capture.title or ""),
            currency="CNY",
            transaction_type=TransactionType.EXPENSE,
            merchant=self._merchant,
            category=_category_for_title(capture.title or ""),
            paid_at=capture.captured_at,
            confidence=0.91,
        )


class EmptyAiClient:
    """Fake AI client that simulates unavailable AI parsing."""

    def parse_capture(self, capture: RawCapture) -> ParsedDraftData | None:
        """Return no AI result."""
        return None


def _amount_for_title(title: str) -> int:
    if "银行" in title:
        return 8800
    if "支付宝" in title:
        return 600
    return 1850


def _category_for_title(title: str) -> str:
    if "支付宝" in title:
        return "交通"
    return "其他"
