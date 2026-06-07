"""Repository protocols used by backend application services."""

from typing import Protocol

from app.domain.models import ConfirmedTransaction, RawCapture, TodaySummary, TransactionDraft


class CaptureRepository(Protocol):
    """Stores raw capture evidence independently from parsing and ledger writes."""

    def save(self, capture: RawCapture) -> RawCapture:
        """Persist a raw capture and return the stored value."""


class DraftRepository(Protocol):
    """Manages pending and reviewed transaction drafts."""

    def save(self, draft: TransactionDraft) -> TransactionDraft:
        """Persist a draft and return the stored value."""

    def find_by_id(self, draft_id: str) -> TransactionDraft | None:
        """Return a draft by id, or None when it does not exist."""

    def list_by_status(self, status: str | None) -> list[TransactionDraft]:
        """Return drafts filtered by optional status."""

    def update(self, draft: TransactionDraft) -> TransactionDraft:
        """Replace an existing draft and return the updated value."""


class TransactionRepository(Protocol):
    """Stores confirmed ledger transactions."""

    def save(self, transaction: ConfirmedTransaction) -> ConfirmedTransaction:
        """Persist a transaction and return the stored value."""

    def list_recent(self, limit: int) -> list[ConfirmedTransaction]:
        """Return recent transactions sorted from newest to oldest."""


class SummaryRepository(Protocol):
    """Provides aggregate read models for dashboards."""

    def today_summary(self) -> TodaySummary:
        """Return today's expense total and pending count."""
