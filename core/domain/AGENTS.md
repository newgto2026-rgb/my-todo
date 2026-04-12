# :core:domain Module Guide

## Role
- Use cases/interactors and repository contracts.
- Domain scheduler contracts.

## Rules
- No Android UI framework dependency.
- Keep use cases small and composable.
- Express failures via `Result` or domain error model.

## Change Checklist
- If repository contract changes, update `core:data` implementations.
- Keep use case names behavior-oriented and test-first.

## Validate
- `./gradlew :core:domain:testDebugUnitTest`
