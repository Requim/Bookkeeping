"""Domain models for the SmartLedger backend."""

from dataclasses import dataclass
from enum import StrEnum


class CaptureSource(StrEnum):
    """Supported raw evidence sources uploaded by the Android client."""

    NOTIFICATION = "NOTIFICATION"
    IMAGE_OCR = "IMAGE_OCR"
    SCREEN_TEXT = "SCREEN_TEXT"
    MANUAL_TEXT = "MANUAL_TEXT"


class TransactionType(StrEnum):
    """Ledger transaction types understood by the backend."""

    EXPENSE = "EXPENSE"
    INCOME = "INCOME"
    TRANSFER = "TRANSFER"
    REFUND = "REFUND"


class DraftStatus(StrEnum):
    """Review status for a parsed transaction draft."""

    PENDING = "PENDING"
    CONFIRMED = "CONFIRMED"
    IGNORED = "IGNORED"


@dataclass(frozen=True)
class RawCapture:
    """Raw payment evidence uploaded by Android before parsing."""

    capture_id: str
    source: CaptureSource
    text: str
    captured_at: int
    app_package: str | None = None
    title: str | None = None
    image_base64: str | None = None


@dataclass(frozen=True)
class TransactionDraft:
    """Parsed candidate that requires user confirmation before entering the ledger."""

    draft_id: str
    amount_cents: int
    currency: str
    transaction_type: TransactionType
    merchant: str
    category: str
    paid_at: int
    confidence: float
    status: DraftStatus
    raw_capture_id: str


@dataclass(frozen=True)
class ConfirmedTransaction:
    """Confirmed ledger transaction stored after user review."""

    transaction_id: str
    amount_cents: int
    currency: str
    transaction_type: TransactionType
    merchant: str
    category: str
    paid_at: int
    note: str


@dataclass(frozen=True)
class DraftConfirmation:
    """User-edited values used to confirm a draft."""

    amount_cents: int
    currency: str
    transaction_type: TransactionType
    merchant: str
    category: str
    paid_at: int
    note: str


@dataclass(frozen=True)
class TodaySummary:
    """Expense summary displayed by the Android dashboard."""

    expense_cents: int
    currency: str
    pending_draft_count: int
