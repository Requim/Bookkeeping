"""OpenAI-compatible HTTP client for backend-only AI parsing."""

import json

import httpx

from app.domain.models import ParsedDraftData, RawCapture, TransactionType
from app.domain.services import AiDraftClient
from app.infrastructure.ai_config import AiParserConfig
from app.infrastructure.ai_prompt import ExpensePromptBuilder


class OpenAiCompatibleDraftClient(AiDraftClient):
    """Calls an OpenAI-compatible chat completions API for draft parsing."""

    def __init__(self, config: AiParserConfig, prompt_builder: ExpensePromptBuilder) -> None:
        self._config = config
        self._prompt_builder = prompt_builder

    def parse_capture(self, capture: RawCapture) -> ParsedDraftData | None:
        """Parse a raw capture using the configured AI model."""
        if not self._config.enabled:
            return None
        response = self._post_chat_completion(capture)
        content = response["choices"][0]["message"]["content"]
        return self._parsed_data(json.loads(content))

    def _post_chat_completion(self, capture: RawCapture) -> dict:
        url = f"{self._config.base_url.rstrip('/')}/chat/completions"
        headers = {"Authorization": f"Bearer {self._config.api_key}"}
        with httpx.Client(timeout=self._config.timeout_seconds) as client:
            response = client.post(url, headers=headers, json=self._payload(capture))
        response.raise_for_status()
        return response.json()

    def _payload(self, capture: RawCapture) -> dict:
        return {
            "model": self._config.model,
            "response_format": {"type": "json_object"},
            "messages": [
                {"role": "system", "content": self._prompt_builder.system_prompt()},
                {"role": "user", "content": self._prompt_builder.user_prompt(capture)},
            ],
        }

    def _parsed_data(self, data: dict) -> ParsedDraftData:
        return ParsedDraftData(
            amount_cents=int(data["amountCents"]),
            currency=str(data.get("currency", "CNY")),
            transaction_type=TransactionType(str(data.get("type", "EXPENSE"))),
            merchant=str(data.get("merchant") or "待确认商户"),
            category=str(data.get("category") or "其他"),
            paid_at=data.get("paidAt"),
            confidence=float(data.get("confidence", 0.5)),
        )
