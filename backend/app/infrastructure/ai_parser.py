"""AI-first parser with explicit local fallback behavior."""

from app.domain.models import DraftStatus, RawCapture, TransactionDraft
from app.domain.services import AiDraftClient, ExpenseDraftParser


class AiExpenseDraftParser(ExpenseDraftParser):
    """Uses backend AI parsing and falls back when no AI result is available."""

    def __init__(
        self,
        ai_client: AiDraftClient,
        fallback_parser: ExpenseDraftParser,
    ) -> None:
        self._ai_client = ai_client
        self._fallback_parser = fallback_parser

    def parse(self, capture: RawCapture) -> TransactionDraft | None:
        """Return an AI parsed draft or the configured fallback draft."""
        parsed = self._ai_client.parse_capture(capture)
        if parsed is None:
            return self._fallback_parser.parse(capture)
        return TransactionDraft(
            draft_id="",
            amount_cents=parsed.amount_cents,
            currency=parsed.currency,
            transaction_type=parsed.transaction_type,
            merchant=parsed.merchant,
            category=parsed.category,
            paid_at=parsed.paid_at or capture.captured_at,
            confidence=parsed.confidence,
            status=DraftStatus.PENDING,
            raw_capture_id=capture.capture_id,
        )
