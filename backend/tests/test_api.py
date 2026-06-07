"""API tests for the FastAPI backend MVP."""

from fastapi.testclient import TestClient

from app.main import create_app


def test_capture_confirm_and_summary_flow() -> None:
    """Upload a capture, confirm it, and verify ledger summary output."""
    client = TestClient(create_app())
    draft = _create_capture(client)

    response = client.patch(
        f"/api/drafts/{draft['draftId']}/confirm",
        json=_confirmation_payload(draft),
    )

    assert response.status_code == 200
    assert response.json()["status"] == "CONFIRMED"
    assert _recent_transactions(client)[0]["merchant"] == "瑞幸咖啡"
    assert client.get("/api/summary/today").json()["expenseCents"] == 1850


def test_list_pending_drafts() -> None:
    """Return uploaded captures as pending drafts."""
    client = TestClient(create_app())
    draft = _create_capture(client)

    response = client.get("/api/drafts?status=PENDING")

    assert response.status_code == 200
    assert response.json()["items"][0]["draftId"] == draft["draftId"]


def test_ignore_draft() -> None:
    """Mark a draft ignored without creating a transaction."""
    client = TestClient(create_app())
    draft = _create_capture(client)

    response = client.patch(f"/api/drafts/{draft['draftId']}/ignore")

    assert response.status_code == 200
    assert response.json()["status"] == "IGNORED"
    assert _recent_transactions(client) == []


def test_confirm_missing_draft_returns_404() -> None:
    """Return 404 when confirming an unknown draft."""
    client = TestClient(create_app())

    response = client.patch("/api/drafts/missing/confirm", json=_confirmation_payload())

    assert response.status_code == 404


def _create_capture(client: TestClient) -> dict:
    response = client.post(
        "/api/captures",
        json={
            "source": "NOTIFICATION",
            "appPackage": "com.tencent.mm",
            "title": "微信支付",
            "text": "商户：瑞幸咖啡 支付成功 ¥18.50",
            "imageBase64": None,
            "capturedAt": 1780819200000,
        },
    )
    assert response.status_code == 200
    return response.json()


def _confirmation_payload(draft: dict | None = None) -> dict:
    return {
        "amountCents": draft["amountCents"] if draft else 1850,
        "currency": "CNY",
        "type": "EXPENSE",
        "merchant": draft["merchant"] if draft else "瑞幸咖啡",
        "category": "餐饮",
        "paidAt": draft["paidAt"] if draft else 1780819200000,
        "note": "",
    }


def _recent_transactions(client: TestClient) -> list[dict]:
    return client.get("/api/transactions?limit=20").json()["items"]
