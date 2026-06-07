"""Dependency wiring for the FastAPI backend MVP."""

from app.application.services import LedgerUseCases
from app.application.use_cases import (
    ConfirmDraftUseCase,
    CreateCaptureUseCase,
    IgnoreDraftUseCase,
    ListDraftsUseCase,
    ListTransactionsUseCase,
    TodaySummaryUseCase,
)
from app.infrastructure.memory_store import create_memory_repositories
from app.infrastructure.simple_parser import SimpleExpenseDraftParser


class BackendContainer:
    """Creates long-lived backend dependencies for route handlers."""

    def __init__(self) -> None:
        self.captures, self.drafts, self.transactions, self.summaries = (
            create_memory_repositories()
        )
        self.parser = SimpleExpenseDraftParser()
        self.use_cases = self._build_use_cases()

    def _build_use_cases(self) -> LedgerUseCases:
        return LedgerUseCases(
            create_capture=CreateCaptureUseCase(self.captures, self.drafts, self.parser),
            list_drafts=ListDraftsUseCase(self.drafts),
            confirm_draft=ConfirmDraftUseCase(self.drafts, self.transactions),
            ignore_draft=IgnoreDraftUseCase(self.drafts),
            list_transactions=ListTransactionsUseCase(self.transactions),
            today_summary=TodaySummaryUseCase(self.summaries),
        )
