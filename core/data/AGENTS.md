# :core:data Module Delta

## Scope
- Repository implementations between `core:domain` contracts and storage/network data sources.
- Cross-layer mapping.

## Module Rules
- Implement only contracts declared in `core:domain`.
- Keep mapper boundaries explicit and side effects localized.
- If repository behavior changes, re-check affected use cases and mapper tests.
