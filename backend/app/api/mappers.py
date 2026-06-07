"""Mapping helpers between API schemas and domain models."""

from app.api.schemas import (
    CaptureRequest,
    ConfirmDraftRequest,
    DraftResponse,
    TodaySummaryResponse,
    TransactionResponse,
)
from app.domain.models import (
    CaptureSource,
    DraftConfirmation,
    RawCapture,
    TodaySummary,
    TransactionDraft,
    TransactionType,
    ConfirmedTransaction,
)


def capture_from_request(request: CaptureRequest) -> RawCapture:
    """Convert upload request data into a raw capture domain model."""
    return RawCapture(
        capture_id="",
        source=CaptureSource(request.source),
        app_package=request.appPackage,
        title=request.title,
        text=request.text,
        image_base64=request.imageBase64,
        captured_at=request.capturedAt,
    )


def confirmation_from_request(request: ConfirmDraftRequest) -> DraftConfirmation:
    """Convert confirmation request data into domain confirmation input."""
    return DraftConfirmation(
        amount_cents=request.amountCents,
        currency=request.currency,
        transaction_type=TransactionType(request.type),
        merchant=request.merchant,
        category=request.category,
        paid_at=request.paidAt,
        note=request.note,
    )


def draft_to_response(draft: TransactionDraft) -> DraftResponse:
    """Convert a domain draft into the public API response schema."""
    return DraftResponse(
        draftId=draft.draft_id,
        amountCents=draft.amount_cents,
        currency=draft.currency,
        type=draft.transaction_type.value,
        merchant=draft.merchant,
        category=draft.category,
        paidAt=draft.paid_at,
        confidence=draft.confidence,
        status=draft.status.value,
    )


def transaction_to_response(transaction: ConfirmedTransaction) -> TransactionResponse:
    """Convert a confirmed transaction into the public API response schema."""
    return TransactionResponse(
        transactionId=transaction.transaction_id,
        amountCents=transaction.amount_cents,
        currency=transaction.currency,
        type=transaction.transaction_type.value,
        merchant=transaction.merchant,
        category=transaction.category,
        paidAt=transaction.paid_at,
        note=transaction.note,
    )


def summary_to_response(summary: TodaySummary) -> TodaySummaryResponse:
    """Convert domain summary data into a public API response."""
    return TodaySummaryResponse(
        expenseCents=summary.expense_cents,
        currency=summary.currency,
        pendingDraftCount=summary.pending_draft_count,
    )
