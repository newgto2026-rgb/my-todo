# :feature:todo:api Module Guide

## Role
- Public todo feature contract exposed to app and other modules.
- Navigation route/entry contract.

## Rules
- Keep API surface minimal and stable.
- Must not depend on `:feature:todo:impl`.

## Change Checklist
- If route/entry contract changes, update `:feature:todo:impl` and `:feature:todo:entry`.
- Prefer type-safe route definitions.

## Validate
- `./gradlew :feature:todo:api:lintDebug`
