"""In-memory repository implementations for the FastAPI MVP."""

from dataclasses import replace
from threading import Lock

from app.domain.models import (
    ConfirmedTransaction,
    DraftStatus,
    RawCapture,
    TodaySummary,
    TransactionDraft,
    TransactionType,
)
from app.infrastructure.id_generator import SequentialIdGenerator


class _LedgerMemoryState:
    def __init__(self) -> None:
        self.captures: dict[str, RawCapture] = {}
        self.drafts: dict[str, TransactionDraft] = {}
        self.transactions: dict[str, ConfirmedTransaction] = {}
        self.capture_ids = SequentialIdGenerator("capture")
        self.draft_ids = SequentialIdGenerator("draft")
        self.transaction_ids = SequentialIdGenerator("txn")
        self.lock = Lock()


class InMemoryCaptureRepository:
    """Stores raw capture evidence in process memory."""

    def __init__(self, state: _LedgerMemoryState) -> None:
        self._state = state

    def save(self, capture: RawCapture) -> RawCapture:
        """Persist raw capture evidence."""
        capture_id = capture.capture_id or self._state.capture_ids.next_id()
        stored = replace(capture, capture_id=capture_id)
        with self._state.lock:
            self._state.captures[capture_id] = stored
        return stored


class InMemoryDraftRepository:
    """Stores transaction drafts in process memory."""

    def __init__(self, state: _LedgerMemoryState) -> None:
        self._state = state

    def save(self, draft: TransactionDraft) -> TransactionDraft:
        """Persist a transaction draft."""
        draft_id = draft.draft_id or self._state.draft_ids.next_id()
        stored = replace(draft, draft_id=draft_id)
        with self._state.lock:
            self._state.drafts[draft_id] = stored
        return stored

    def find_by_id(self, draft_id: str) -> TransactionDraft | None:
        """Return a draft by id, or None when absent."""
        with self._state.lock:
            return self._state.drafts.get(draft_id)

    def list_by_status(self, status: str | None) -> list[TransactionDraft]:
        """Return drafts filtered by optional status string."""
        with self._state.lock:
            drafts = list(self._state.drafts.values())
        return [draft for draft in drafts if self._matches_status(draft, status)]

    def update(self, draft: TransactionDraft) -> TransactionDraft:
        """Replace an existing draft."""
        with self._state.lock:
            if draft.draft_id not in self._state.drafts:
                raise LookupError(f"Draft {draft.draft_id} does not exist.")
            self._state.drafts[draft.draft_id] = draft
        return draft

    def _matches_status(self, draft: TransactionDraft, status: str | None) -> bool:
        if status is None:
            return True
        return draft.status.value == status.upper()


class InMemoryTransactionRepository:
    """Stores confirmed transactions in process memory."""

    def __init__(self, state: _LedgerMemoryState) -> None:
        self._state = state

    def save(self, transaction: ConfirmedTransaction) -> ConfirmedTransaction:
        """Persist a confirmed transaction."""
        transaction_id = transaction.transaction_id or self._state.transaction_ids.next_id()
        stored = replace(transaction, transaction_id=transaction_id)
        with self._state.lock:
            self._state.transactions[transaction_id] = stored
        return stored

    def list_recent(self, limit: int) -> list[ConfirmedTransaction]:
        """Return recent transactions sorted from newest to oldest."""
        with self._state.lock:
            transactions = list(self._state.transactions.values())
        ordered = sorted(transactions, key=lambda item: item.paid_at, reverse=True)
        return ordered[:limit]


class InMemorySummaryRepository:
    """Builds dashboard summaries from in-memory drafts and transactions."""

    def __init__(self, state: _LedgerMemoryState) -> None:
        self._state = state

    def today_summary(self) -> TodaySummary:
        """Return current MVP expense summary and pending count."""
        with self._state.lock:
            transactions = list(self._state.transactions.values())
            drafts = list(self._state.drafts.values())
        expense_cents = sum(self._expense_amounts(transactions))
        pending_count = sum(1 for draft in drafts if draft.status == DraftStatus.PENDING)
        return TodaySummary(expense_cents, "CNY", pending_count)

    def _expense_amounts(self, transactions: list[ConfirmedTransaction]) -> list[int]:
        return [
            item.amount_cents
            for item in transactions
            if item.transaction_type == TransactionType.EXPENSE
        ]


def create_memory_repositories() -> tuple[
    InMemoryCaptureRepository,
    InMemoryDraftRepository,
    InMemoryTransactionRepository,
    InMemorySummaryRepository,
]:
    """Create independent repositories backed by shared in-memory state."""
    state = _LedgerMemoryState()
    return (
        InMemoryCaptureRepository(state),
        InMemoryDraftRepository(state),
        InMemoryTransactionRepository(state),
        InMemorySummaryRepository(state),
    )
