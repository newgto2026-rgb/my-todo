# :core:data Module Guide

## Role
- Repository implementations bridging domain and data sources.
- Mapping between entities/models.

## Rules
- Implement only domain contracts from `core:domain`.
- Keep mapping explicit and side-effect boundaries clear.

## Change Checklist
- If repository behavior changes, re-check related use cases.
- Keep mapper tests updated for schema/model changes.

## Validate
- `./gradlew :core:data:testDebugUnitTest`
- `./gradlew :core:data:lintDebug`
