# :feature:todo:entry Module Delta

## Scope
- Wiring module for todo feature entry binding used by app composition.

## Module Rules
- Keep only DI/wiring concerns.
- Avoid business logic and UI implementation.
- If API or impl class names change, update Hilt bindings accordingly.
- Keep this module limited to binding `impl` into app-consumed contracts.
