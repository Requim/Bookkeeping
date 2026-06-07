"""Contract tests that protect Android DTO compatibility."""

from fastapi.testclient import TestClient

from app.main import create_app


def test_backend_responses_match_android_dto_fields() -> None:
    """Verify backend JSON keys match the Android remote DTO contract."""
    client = TestClient(create_app())
    draft = _create_capture(client)

    assert set(draft) == _draft_fields()
    assert set(client.get("/api/drafts?status=PENDING").json()["items"][0]) == _draft_fields()
    assert set(_confirm(client, draft)) == {"transactionId", "status"}
    assert set(client.get("/api/transactions?limit=20").json()["items"][0]) == _transaction_fields()
    assert set(client.get("/api/summary/today").json()) == _summary_fields()


def test_ignore_response_matches_android_dto_fields() -> None:
    """Verify ignore response keys match the Android remote DTO contract."""
    client = TestClient(create_app())
    draft = _create_capture(client)

    response = client.patch(f"/api/drafts/{draft['draftId']}/ignore")

    assert response.status_code == 200
    assert set(response.json()) == {"draftId", "status"}


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


def _confirm(client: TestClient, draft: dict) -> dict:
    response = client.patch(
        f"/api/drafts/{draft['draftId']}/confirm",
        json={
            "amountCents": draft["amountCents"],
            "currency": draft["currency"],
            "type": draft["type"],
            "merchant": draft["merchant"],
            "category": draft["category"],
            "paidAt": draft["paidAt"],
            "note": "",
        },
    )
    assert response.status_code == 200
    return response.json()


def _draft_fields() -> set[str]:
    return {
        "draftId",
        "amountCents",
        "currency",
        "type",
        "merchant",
        "category",
        "paidAt",
        "confidence",
        "status",
    }


def _transaction_fields() -> set[str]:
    return {
        "transactionId",
        "amountCents",
        "currency",
        "type",
        "merchant",
        "category",
        "paidAt",
        "note",
    }


def _summary_fields() -> set[str]:
    return {"expenseCents", "currency", "pendingDraftCount"}
