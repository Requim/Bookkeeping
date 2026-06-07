"""Application use cases for the SmartLedger backend MVP."""

from dataclasses import replace

from app.domain.models import (
    DraftConfirmation,
    DraftStatus,
    RawCapture,
    TodaySummary,
    TransactionDraft,
    ConfirmedTransaction,
)
from app.domain.repositories import (
    CaptureRepository,
    DraftRepository,
    SummaryRepository,
    TransactionRepository,
)
from app.domain.services import ExpenseDraftParser


class CreateCaptureUseCase:
    """Stores raw capture evidence and creates a pending draft when parsing succeeds."""

    def __init__(
        self,
        captures: CaptureRepository,
        drafts: DraftRepository,
        parser: ExpenseDraftParser,
    ) -> None:
        self._captures = captures
        self._drafts = drafts
        self._parser = parser

    def execute(self, capture: RawCapture) -> TransactionDraft | None:
        """Persist raw evidence and return a new draft when one is detected."""
        stored_capture = self._captures.save(capture)
        draft = self._parser.parse(stored_capture)
        if draft is None:
            return None
        return self._drafts.save(draft)


class ListDraftsUseCase:
    """Reads drafts for review screens."""

    def __init__(self, drafts: DraftRepository) -> None:
        self._drafts = drafts

    def execute(self, status: str | None) -> list[TransactionDraft]:
        """Return drafts filtered by optional status."""
        return self._drafts.list_by_status(status)


class ConfirmDraftUseCase:
    """Confirms a pending draft and creates a final ledger transaction."""

    def __init__(
        self,
        drafts: DraftRepository,
        transactions: TransactionRepository,
    ) -> None:
        self._drafts = drafts
        self._transactions = transactions

    def execute(self, draft_id: str, input_data: DraftConfirmation) -> ConfirmedTransaction:
        """Confirm a draft with user-reviewed values."""
        self._require_draft(draft_id)
        transaction = self._transactions.save(self._to_transaction(input_data))
        self._drafts.update(self._confirmed_draft(draft_id, input_data))
        return transaction

    def _require_draft(self, draft_id: str) -> TransactionDraft:
        draft = self._drafts.find_by_id(draft_id)
        if draft is None:
            raise LookupError(f"Draft {draft_id} does not exist.")
        return draft

    def _confirmed_draft(
        self,
        draft_id: str,
        input_data: DraftConfirmation,
    ) -> TransactionDraft:
        draft = self._require_draft(draft_id)
        return replace(
            draft,
            amount_cents=input_data.amount_cents,
            currency=input_data.currency,
            transaction_type=input_data.transaction_type,
            merchant=input_data.merchant,
            category=input_data.category,
            paid_at=input_data.paid_at,
            status=DraftStatus.CONFIRMED,
        )

    def _to_transaction(self, input_data: DraftConfirmation) -> ConfirmedTransaction:
        return ConfirmedTransaction(
            transaction_id="",
            amount_cents=input_data.amount_cents,
            currency=input_data.currency,
            transaction_type=input_data.transaction_type,
            merchant=input_data.merchant,
            category=input_data.category,
            paid_at=input_data.paid_at,
            note=input_data.note,
        )


class IgnoreDraftUseCase:
    """Marks a draft as ignored without creating a transaction."""

    def __init__(self, drafts: DraftRepository) -> None:
        self._drafts = drafts

    def execute(self, draft_id: str) -> TransactionDraft:
        """Ignore a draft and return its updated state."""
        draft = self._drafts.find_by_id(draft_id)
        if draft is None:
            raise LookupError(f"Draft {draft_id} does not exist.")
        return self._drafts.update(replace(draft, status=DraftStatus.IGNORED))


class ListTransactionsUseCase:
    """Reads recent confirmed transactions."""

    def __init__(self, transactions: TransactionRepository) -> None:
        self._transactions = transactions

    def execute(self, limit: int) -> list[ConfirmedTransaction]:
        """Return recent transactions with a conservative limit."""
        return self._transactions.list_recent(max(1, min(limit, 100)))


class TodaySummaryUseCase:
    """Reads today's expense summary."""

    def __init__(self, summaries: SummaryRepository) -> None:
        self._summaries = summaries

    def execute(self) -> TodaySummary:
        """Return the dashboard summary."""
        return self._summaries.today_summary()
