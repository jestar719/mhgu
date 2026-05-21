#!/usr/bin/env python3
"""Build a reusable seed lexicon from existing translated mapping files."""

from __future__ import annotations

import json
import sys
from pathlib import Path
from typing import Any


ROOT = Path(__file__).resolve().parents[2]
TRANSLATED_DIR = ROOT / "temp" / "translated"


def load_json(path: Path) -> Any:
    with path.open("r", encoding="utf-8") as handle:
        return json.load(handle)


def collect_pairs(value: Any) -> dict[str, str]:
    pairs: dict[str, str] = {}
    if isinstance(value, dict):
        for key, item in value.items():
            if isinstance(key, str) and isinstance(item, str) and key != item:
                pairs[key] = item
    return pairs


def main() -> None:
    lexicon: dict[str, str] = {}
    for path in sorted(TRANSLATED_DIR.rglob("*.json")):
        try:
            value = load_json(path)
        except json.JSONDecodeError as exc:
            rel = path.relative_to(ROOT)
            print(f"skipped non-strict JSON: {rel}: {exc}", file=sys.stderr)
            continue
        lexicon.update(collect_pairs(value))

    print(json.dumps(lexicon, ensure_ascii=False, indent=2, sort_keys=True))


if __name__ == "__main__":
    main()
