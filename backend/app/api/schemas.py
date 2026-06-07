"""HTTP request and response schemas for the FastAPI ledger API."""

from pydantic import BaseModel, Field


class CaptureRequest(BaseModel):
    """Request body used when Android uploads raw payment evidence."""

    source: str = Field(description="Raw capture source, such as NOTIFICATION or IMAGE_OCR.")
    appPackage: str | None = Field(default=None, description="Android package that emitted it.")
    title: str | None = Field(default=None, description="Notification title or optional label.")
    text: str = Field(description="Notification, OCR, screen, or manual text.")
    imageBase64: str | None = Field(default=None, description="Optional image content reference.")
    capturedAt: int = Field(description="Capture timestamp in epoch milliseconds.")


class DraftResponse(BaseModel):
    """Response item for a transaction draft awaiting user review."""

    draftId: str = Field(description="Backend draft id.")
    amountCents: int = Field(description="Parsed amount in cents.")
    currency: str = Field(description="ISO currency code.")
    type: str = Field(description="Transaction type, for example EXPENSE.")
    merchant: str = Field(description="Parsed or fallback merchant name.")
    category: str = Field(description="Suggested category name.")
    paidAt: int = Field(description="Payment timestamp in epoch milliseconds.")
    confidence: float = Field(description="Parser confidence from 0 to 1.")
    status: str = Field(description="Draft status.")


class DraftListResponse(BaseModel):
    """Response wrapper for draft list endpoints."""

    items: list[DraftResponse] = Field(description="Draft items.")


class ConfirmDraftRequest(BaseModel):
    """Request body used when a user confirms a draft."""

    amountCents: int = Field(description="Reviewed amount in cents.")
    currency: str = Field(description="Reviewed currency code.")
    type: str = Field(description="Reviewed transaction type.")
    merchant: str = Field(description="Reviewed merchant name.")
    category: str = Field(description="Reviewed category name.")
    paidAt: int = Field(description="Reviewed payment timestamp.")
    note: str = Field(default="", description="Optional user note.")


class ConfirmDraftResponse(BaseModel):
    """Response body returned after a draft becomes a transaction."""

    transactionId: str = Field(description="Confirmed transaction id.")
    status: str = Field(description="Final draft status.")


class IgnoreDraftResponse(BaseModel):
    """Response body returned after ignoring a draft."""

    draftId: str = Field(description="Ignored draft id.")
    status: str = Field(description="Final draft status.")


class TransactionResponse(BaseModel):
    """Response item for a confirmed transaction."""

    transactionId: str = Field(description="Backend transaction id.")
    amountCents: int = Field(description="Confirmed amount in cents.")
    currency: str = Field(description="ISO currency code.")
    type: str = Field(description="Transaction type.")
    merchant: str = Field(description="Merchant name.")
    category: str = Field(description="Category name.")
    paidAt: int = Field(description="Payment timestamp in epoch milliseconds.")
    note: str = Field(description="Optional transaction note.")


class TransactionListResponse(BaseModel):
    """Response wrapper for recent transaction queries."""

    items: list[TransactionResponse] = Field(description="Recent transactions.")


class TodaySummaryResponse(BaseModel):
    """Response body for today's dashboard summary."""

    expenseCents: int = Field(description="Today's confirmed expense total in cents.")
    currency: str = Field(description="Summary currency.")
    pendingDraftCount: int = Field(description="Number of pending drafts.")
