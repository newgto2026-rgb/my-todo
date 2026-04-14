# :feature:calendar:api Module Delta

## Scope
- Public contracts/routes for calendar feature navigation.

## Module Rules
- Keep route contracts type-safe and stable.
- Keep this module free from implementation details.
- Expose only contract types required by consumers.
- Use `implementation` for internal dependencies; use `api` only for intentionally re-exported contract dependencies.
