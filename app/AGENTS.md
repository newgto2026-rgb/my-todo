# :app Module Delta

## Scope
- App shell, startup, top-level navigation, and Android entry points.
- Runtime integration (notifications, workers, receivers).

## Module Rules
- Depend on `feature:*:api`, `feature:*:entry`, and `core:*` only.
- Do not implement feature internals in `:app`.
- Keep `:app` focused on composition and runtime integration; do not move reusable business logic here.
- If start destination or top-level navigation changes, verify feature-entry wiring.
- For reminders/workers, validate runtime permission and notification channel behavior.
