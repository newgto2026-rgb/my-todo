# :feature:todo:entry Module Guide

## Role
- Wiring module that binds todo feature entry implementation for app composition.

## Rules
- Keep only DI/wiring concerns.
- Avoid business logic or UI implementation here.

## Change Checklist
- If API/impl class names change, update Hilt binding.
- Ensure app still resolves feature entry set correctly.

## Validate
- `./gradlew :feature:todo:entry:lintDebug`
