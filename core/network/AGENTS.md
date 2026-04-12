# :core:network Module Guide

## Role
- Networking foundation (API clients, DTO contracts, interceptors).

## Rules
- Keep transport models separate from domain models.
- Prefer explicit serialization contracts.

## Change Checklist
- If API DTO changes, update mappers in `core:data`.
- Keep timeout/retry/auth behavior centralized.

## Validate
- `./gradlew :core:network:lintDebug`
