#!/usr/bin/env python3
from __future__ import annotations

import argparse
import json
import re
from pathlib import Path


ROOT = Path(__file__).resolve().parents[2]
TEMP_TRANSLATION_WEAPON = ROOT / "temp" / "translation" / "weapon"
TEMP_TRANSLATED_WEAPON = ROOT / "temp" / "translated" / "weapon"
TEMP_SUMMARY_WEAPON = ROOT / "temp" / "summary" / "weapon"
TEMP_INFOS = ROOT / "temp" / "infos" / "1.json"
HTML_ROOT = ROOT / "app" / "src" / "main" / "assets" / "mhxx"

IDA_LINK_PATTERNS = [
    re.compile(r'<a href="\.\./(ida/\d+\.html)">.*?</a> x\d+<br>'),
    re.compile(
        r'<span style="background-color:#.*?;">入手端材：<a href="\.\./(ida/\d+\.html)">.*?</a> x\d+</span><br>'
    ),
]


def load_json(path: Path):
    with path.open("r", encoding="utf-8-sig") as fh:
        return json.load(fh)


def dump_json(path: Path, payload) -> None:
    text = json.dumps(payload, ensure_ascii=False, separators=(",", ":"))
    path.write_text(text, encoding="utf-8")


def weapon_names() -> list[str]:
    names: list[str] = []
    for path in sorted(TEMP_TRANSLATED_WEAPON.glob("*.json")):
        stem = path.stem
        if stem.endswith("TransBean") or stem == "通用部分":
            continue
        names.append(stem)
    return names


def validate_weapon_files(name: str) -> tuple[list[str], dict[str, str]]:
    source_path = TEMP_TRANSLATION_WEAPON / f"{name}.json"
    translated_path = TEMP_TRANSLATED_WEAPON / f"{name}.json"
    source_terms = load_json(source_path)
    translated_map = load_json(translated_path)

    source_keys = set(source_terms)
    translated_keys = set(translated_map.keys())
    missing = sorted(source_keys - translated_keys)
    extra = sorted(translated_keys - source_keys)
    if missing or extra:
        details = []
        if missing:
            details.append(f"missing={len(missing)}")
        if extra:
            details.append(f"extra={len(extra)}")
        raise SystemExit(f"{name}: translation key drift detected ({', '.join(details)})")
    return source_terms, translated_map


def load_info_entry(name: str) -> dict:
    infos = load_json(TEMP_INFOS)
    for entry in infos:
        if entry.get("mName") == name:
            return entry
    raise SystemExit(f"{name}: not found in {TEMP_INFOS}")


def collect_data_urls(name: str) -> list[str]:
    entry = load_info_entry(name)
    urls: list[str] = []
    for label, url in entry.get("mData", {}).items():
        if "操作" in label:
            continue
        urls.append(url)
    return sorted(set(urls))


def collect_summary_urls(name: str) -> list[str]:
    summary_path = TEMP_SUMMARY_WEAPON / f"{name}.json"
    summary_map = load_json(summary_path)
    return sorted(set(summary_map.values()))


def build_transbean(name: str) -> dict:
    _, translated_map = validate_weapon_files(name)
    urls = sorted(set(collect_data_urls(name) + collect_summary_urls(name)))
    return {"urls": urls, "texts": translated_map}


def transbean_path(name: str) -> Path:
    return TEMP_TRANSLATED_WEAPON / f"{name}TransBean.json"


def write_transbean(name: str) -> Path:
    path = transbean_path(name)
    dump_json(path, build_transbean(name))
    return path


def replacement_pairs(texts: dict[str, str]) -> list[tuple[str, str]]:
    return sorted(texts.items(), key=lambda item: len(item[0]), reverse=True)


def translate_html_file(
    path: Path,
    pairs: list[tuple[str, str]],
    collect_extras: bool,
) -> set[str]:
    original = path.read_text(encoding="utf-8-sig")
    extra_urls: set[str] = set()
    if collect_extras and "/ida/" in path.as_posix():
        for pattern in IDA_LINK_PATTERNS:
            extra_urls.update(match.group(1) for match in pattern.finditer(original))

    updated = original
    for source, target in pairs:
        updated = updated.replace(source, target)
    if updated != original:
        path.write_text(updated, encoding="utf-8")
    return extra_urls


def compile_weapon(name: str, write_manifest: bool) -> tuple[int, int]:
    if write_manifest or not transbean_path(name).exists():
        write_transbean(name)
    bean = load_json(transbean_path(name))
    pairs = replacement_pairs(bean["texts"])

    initial_urls = list(bean["urls"])
    initial_url_set = set(initial_urls)
    queue = list(initial_urls)
    seen: set[str] = set()
    translated_count = 0

    while queue:
        rel_url = queue.pop(0)
        if rel_url in seen:
            continue
        seen.add(rel_url)
        path = HTML_ROOT / rel_url
        if not path.exists():
            raise SystemExit(f"{name}: missing html file {path}")
        extras = translate_html_file(path, pairs, collect_extras=rel_url in initial_url_set)
        translated_count += 1
        for extra in sorted(extras):
            if extra not in seen:
                queue.append(extra)

    return translated_count, len(bean["texts"])


def parse_args() -> argparse.Namespace:
    parser = argparse.ArgumentParser(description="Generate and compile MHGU weapon translations.")
    subparsers = parser.add_subparsers(dest="command", required=True)

    make_bean = subparsers.add_parser("make-bean", help="Generate <weapon>TransBean.json")
    make_bean.add_argument("--weapon", required=True, help="Weapon name, e.g. 片手剑")

    compile_cmd = subparsers.add_parser("compile", help="Apply weapon translations into HTML")
    scope = compile_cmd.add_mutually_exclusive_group(required=True)
    scope.add_argument("--weapon", help="Weapon name, e.g. 片手剑")
    scope.add_argument("--all", action="store_true", help="Compile all translated weapons")
    compile_cmd.add_argument(
        "--write-trans-bean",
        action="store_true",
        help="Regenerate the TransBean before compiling",
    )
    return parser.parse_args()


def main() -> None:
    args = parse_args()
    if args.command == "make-bean":
        path = write_transbean(args.weapon)
        print(f"wrote {path.relative_to(ROOT)}")
        return

    names = weapon_names() if args.all else [args.weapon]
    for name in names:
        translated_count, term_count = compile_weapon(name, args.write_trans_bean)
        print(f"{name}: compiled {translated_count} html files with {term_count} terms")


if __name__ == "__main__":
    main()
