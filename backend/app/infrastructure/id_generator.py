"""Identifier generation for in-memory backend storage."""

from itertools import count
from threading import Lock


class SequentialIdGenerator:
    """Generates readable ids with a stable prefix for MVP storage."""

    def __init__(self, prefix: str) -> None:
        self._prefix = prefix
        self._counter = count(1)
        self._lock = Lock()

    def next_id(self) -> str:
        """Return the next id in prefix_001 format."""
        with self._lock:
            value = next(self._counter)
        return f"{self._prefix}_{value:03d}"
