"""Domain service protocols for replaceable backend behavior."""

from typing import Protocol

from app.domain.models import RawCapture, TransactionDraft


class ExpenseDraftParser(Protocol):
    """Parses raw capture evidence into a transaction draft."""

    def parse(self, capture: RawCapture) -> TransactionDraft | None:
        """Return a draft when the capture looks like a ledger item."""
