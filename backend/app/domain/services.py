"""Domain service protocols for replaceable backend behavior."""

from typing import Protocol

from app.domain.models import ParsedDraftData, RawCapture, TransactionDraft


class ExpenseDraftParser(Protocol):
    """Parses raw capture evidence into a transaction draft."""

    def parse(self, capture: RawCapture) -> TransactionDraft | None:
        """Return a draft when the capture looks like a ledger item."""


class AiDraftClient(Protocol):
    """Calls an AI model and returns structured draft fields."""

    def parse_capture(self, capture: RawCapture) -> ParsedDraftData | None:
        """Parse raw capture evidence into structured draft fields."""
