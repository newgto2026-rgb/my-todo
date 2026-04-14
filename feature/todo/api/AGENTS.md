# :feature:todo:api Module Delta

## Scope
- Public todo feature contracts and route/entry contracts.

## Module Rules
- Keep API surface minimal and stable.
- Must not depend on `:feature:todo:impl`.
- If route/entry contracts change, update `:feature:todo:impl` and `:feature:todo:entry`.
- Expose only consumer-facing contract types.
- Use `implementation` by default; reserve `api` for dependencies that are intentionally part of this module's public contract.
