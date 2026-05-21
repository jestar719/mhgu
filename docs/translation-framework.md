# MHGU Translation Framework

This project keeps translation data separate from the old Android UI. The important runtime
and translation data is under `temp/`; frontend experiments should not delete or overwrite it.

## Data Areas

- `temp/translation/`: extracted source terms grouped by domain.
- `temp/translated/`: human-maintained translation maps and compiled translated records.
- `temp/summary/`: generated summary data consumed by parsers/tests and data packaging.
- `app/src/main/assets/mhxx/`: offline MHXX HTML source pages.

## Rules

- Treat `temp/translated/` as the source of truth for human translation work.
- Keep generated UI/platform files separate from translation data.
- Do not bulk replace Chinese values without reviewing the original Japanese source and nearby
  terms in the same tree.
- When adding tools, write them so they read from `temp/` and produce reviewable JSON or
  Markdown reports instead of mutating translations by default.

## Helper Scripts

- `tools/translation/build_translation_inventory.py` prints counts and coverage for the
  current translation files.
- `tools/translation/build_seed_lexicon.py` builds a small reusable lexicon from existing
  `temp/translated` mappings.
- `tools/translation/crawl_reference_sources.py` fetches reference pages listed in
  `tools/translation/reference_sources.json` into a local cache when network access is needed.
