"""FastAPI application entry point for the SmartLedger backend."""

from fastapi import FastAPI

from app.api.routes import router
from app.infrastructure.container import BackendContainer


def create_app() -> FastAPI:
    """Create and configure the FastAPI application."""
    app = FastAPI(title="SmartLedger Backend", version="0.1.0")
    app.state.container = BackendContainer()
    app.include_router(router)
    return app


app = create_app()
