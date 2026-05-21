#!/usr/bin/env python3
"""Report translation data coverage without mutating repository files."""

from __future__ import annotations

import json
from pathlib import Path
from typing import Any


ROOT = Path(__file__).resolve().parents[2]
SOURCE_DIR = ROOT / "temp" / "translation"
TRANSLATED_DIR = ROOT / "temp" / "translated"
SUMMARY_DIR = ROOT / "temp" / "summary"


def load_json(path: Path) -> Any:
    with path.open("r", encoding="utf-8") as handle:
        return json.load(handle)


def count_entries(value: Any) -> int:
    if isinstance(value, dict):
        return len(value)
    if isinstance(value, list):
        return len(value)
    return 1


def iter_json_files(root: Path) -> list[Path]:
    if not root.exists():
        return []
    return sorted(path for path in root.rglob("*.json") if path.is_file())


def summarize(root: Path) -> tuple[int, int, list[str]]:
    files = iter_json_files(root)
    entries = 0
    skipped: list[str] = []
    for path in files:
        try:
            entries += count_entries(load_json(path))
        except json.JSONDecodeError as exc:
            rel = path.relative_to(ROOT)
            skipped.append(f"{rel}: {exc}")
    return len(files), entries, skipped


def main() -> None:
    for label, root in (
        ("source", SOURCE_DIR),
        ("translated", TRANSLATED_DIR),
        ("summary", SUMMARY_DIR),
    ):
        file_count, entry_count, skipped = summarize(root)
        print(f"{label}: {file_count} files, {entry_count} top-level entries")
        for item in skipped:
            print(f"  skipped non-strict JSON: {item}")

    print()
    for path in iter_json_files(TRANSLATED_DIR):
        rel = path.relative_to(ROOT)
        try:
            entries = count_entries(load_json(path))
        except json.JSONDecodeError:
            entries = -1
        print(f"{rel}: {entries}")


if __name__ == "__main__":
    main()
