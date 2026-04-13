# :feature:calendar:impl Module Guide

## Role
- Calendar feature implementation: screen composition and feature entry wiring.

## Rules
- Keep screen state-driven and ready for `UiState + Action + SideEffect` extension.
- Keep user-visible text in resources.

## Validate
- `./gradlew :feature:calendar:impl:testDebugUnitTest`
- `./gradlew :feature:calendar:impl:lintDebug`
