"""Application service container for API wiring."""

from dataclasses import dataclass

from app.application.use_cases import (
    ConfirmDraftUseCase,
    CreateCaptureUseCase,
    IgnoreDraftUseCase,
    ListDraftsUseCase,
    ListTransactionsUseCase,
    TodaySummaryUseCase,
)


@dataclass(frozen=True)
class LedgerUseCases:
    """Groups ledger use cases without exposing infrastructure to API routes."""

    create_capture: CreateCaptureUseCase
    list_drafts: ListDraftsUseCase
    confirm_draft: ConfirmDraftUseCase
    ignore_draft: IgnoreDraftUseCase
    list_transactions: ListTransactionsUseCase
    today_summary: TodaySummaryUseCase
