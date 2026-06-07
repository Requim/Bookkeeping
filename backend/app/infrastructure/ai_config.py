"""Configuration for backend-only AI parsing."""

import os
from dataclasses import dataclass


@dataclass(frozen=True)
class AiParserConfig:
    """Environment-backed configuration for the AI parser client."""

    api_key: str | None
    base_url: str
    model: str
    timeout_seconds: float

    @property
    def enabled(self) -> bool:
        """Return whether backend AI parsing is configured."""
        return bool(self.api_key)


def load_ai_parser_config() -> AiParserConfig:
    """Load AI parser settings from backend environment variables."""
    return AiParserConfig(
        api_key=os.getenv("LEDGER_AI_API_KEY"),
        base_url=os.getenv("LEDGER_AI_BASE_URL", "https://api.openai.com/v1"),
        model=os.getenv("LEDGER_AI_MODEL", "gpt-4o-mini"),
        timeout_seconds=float(os.getenv("LEDGER_AI_TIMEOUT_SECONDS", "20")),
    )
