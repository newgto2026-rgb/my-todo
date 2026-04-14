# :feature:calendar:entry Module Delta

## Scope
- Hilt multibinding registration for calendar feature entry.

## Module Rules
- Bind only via `AppFeatureEntry` set multibinding.
- Keep `app` decoupled from `:feature:calendar:impl`.
- Keep this module DI-only; do not add screen logic or feature business logic.
