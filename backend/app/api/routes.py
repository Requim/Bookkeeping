"""FastAPI routes for capture, draft, transaction, and summary workflows."""

from fastapi import APIRouter, HTTPException, Query, Request, status

from app.api.mappers import (
    capture_from_request,
    confirmation_from_request,
    draft_to_response,
    summary_to_response,
    transaction_to_response,
)
from app.api.schemas import (
    CaptureRequest,
    ConfirmDraftRequest,
    ConfirmDraftResponse,
    DraftListResponse,
    DraftResponse,
    IgnoreDraftResponse,
    TodaySummaryResponse,
    TransactionListResponse,
)
from app.application.services import LedgerUseCases

router = APIRouter(prefix="/api")


def _use_cases(request: Request) -> LedgerUseCases:
    return request.app.state.container.use_cases


@router.post("/captures", response_model=DraftResponse | IgnoreDraftResponse)
def create_capture(request_body: CaptureRequest, request: Request):
    """Upload raw capture evidence and return a pending draft when parsed."""
    use_cases = _use_cases(request)
    try:
        draft = use_cases.create_capture.execute(capture_from_request(request_body))
    except ValueError as exc:
        raise HTTPException(status.HTTP_422_UNPROCESSABLE_ENTITY, str(exc)) from exc
    if draft is None:
        return IgnoreDraftResponse(draftId="", status="IGNORED")
    return draft_to_response(draft)


@router.get("/drafts", response_model=DraftListResponse)
def list_drafts(request: Request, status: str | None = Query(default="PENDING")):
    """Return backend drafts filtered by optional status."""
    drafts = _use_cases(request).list_drafts.execute(status)
    return DraftListResponse(items=[draft_to_response(item) for item in drafts])


@router.patch("/drafts/{draft_id}/confirm", response_model=ConfirmDraftResponse)
def confirm_draft(
    draft_id: str,
    request_body: ConfirmDraftRequest,
    request: Request,
):
    """Confirm a reviewed draft and create a transaction."""
    try:
        transaction = _use_cases(request).confirm_draft.execute(
            draft_id,
            confirmation_from_request(request_body),
        )
    except LookupError as exc:
        raise HTTPException(status.HTTP_404_NOT_FOUND, str(exc)) from exc
    return ConfirmDraftResponse(transactionId=transaction.transaction_id, status="CONFIRMED")


@router.patch("/drafts/{draft_id}/ignore", response_model=IgnoreDraftResponse)
def ignore_draft(draft_id: str, request: Request):
    """Ignore a draft without creating a transaction."""
    try:
        draft = _use_cases(request).ignore_draft.execute(draft_id)
    except LookupError as exc:
        raise HTTPException(status.HTTP_404_NOT_FOUND, str(exc)) from exc
    return IgnoreDraftResponse(draftId=draft.draft_id, status=draft.status.value)


@router.get("/transactions", response_model=TransactionListResponse)
def list_transactions(request: Request, limit: int = Query(default=20, ge=1, le=100)):
    """Return recent confirmed transactions."""
    transactions = _use_cases(request).list_transactions.execute(limit)
    return TransactionListResponse(
        items=[transaction_to_response(item) for item in transactions]
    )


@router.get("/summary/today", response_model=TodaySummaryResponse)
def today_summary(request: Request):
    """Return today's expense summary for the dashboard."""
    summary = _use_cases(request).today_summary.execute()
    return summary_to_response(summary)
