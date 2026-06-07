"""Prompt construction for structured expense draft parsing."""

from app.domain.models import RawCapture


class ExpensePromptBuilder:
    """Builds prompts for payment text parsing without leaking into routes."""

    def system_prompt(self) -> str:
        """Return the stable system instruction for the AI parser."""
        return (
            "你是记账解析服务，只返回 JSON。"
            "从支付通知或账单文本中提取 amountCents, currency, type, "
            "merchant, category, paidAt, confidence。"
            "type 只能是 EXPENSE, INCOME, TRANSFER, REFUND。"
            "category 默认从 餐饮、交通、购物、住房、娱乐、医疗、学习、"
            "转账、人情、其他 中选择。"
        )

    def user_prompt(self, capture: RawCapture) -> str:
        """Return user content containing only the raw capture fields."""
        return (
            f"source: {capture.source.value}\n"
            f"appPackage: {capture.app_package or ''}\n"
            f"title: {capture.title or ''}\n"
            f"capturedAt: {capture.captured_at}\n"
            f"text:\n{capture.text}"
        )
