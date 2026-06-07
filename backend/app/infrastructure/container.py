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
from app.infrastructure.ai_config import load_ai_parser_config
from app.infrastructure.ai_parser import AiExpenseDraftParser
from app.infrastructure.ai_prompt import ExpensePromptBuilder
from app.infrastructure.memory_store import create_memory_repositories
from app.infrastructure.openai_compatible_client import OpenAiCompatibleDraftClient
from app.infrastructure.simple_parser import SimpleExpenseDraftParser


class BackendContainer:
    """Creates long-lived backend dependencies for route handlers."""

    def __init__(self) -> None:
        self.captures, self.drafts, self.transactions, self.summaries = (
            create_memory_repositories()
        )
        self.fallback_parser = SimpleExpenseDraftParser()
        self.ai_client = OpenAiCompatibleDraftClient(
            load_ai_parser_config(),
            ExpensePromptBuilder(),
        )
        self.parser = AiExpenseDraftParser(self.ai_client, self.fallback_parser)
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
