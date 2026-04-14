# :core:testing Module Delta

## Scope
- Shared test rules, fakes, and utilities.

## Module Rules
- Keep helpers deterministic and lightweight.
- Avoid feature-specific coupling in shared fakes.
- If repository contracts change, update shared fakes first.
- Keep dispatcher/time control utilities explicit.
