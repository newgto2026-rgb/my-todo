# :core:network Module Delta

## Scope
- Networking foundation: API clients, DTO contracts, and interceptors.

## Module Rules
- Keep transport models separate from domain models.
- Keep serialization contracts explicit.
- If DTO changes, update `core:data` mappers in the same change.
- Keep timeout/retry/auth behavior centralized.
