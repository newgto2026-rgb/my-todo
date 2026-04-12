# :core:datastore Module Guide

## Role
- Preference persistence and user setting data sources.

## Rules
- Expose clear read/write APIs.
- Keep key naming stable and migration-aware.

## Change Checklist
- If preference keys change, add migration/compat handling.
- Keep coroutine/flow threading behavior predictable.

## Validate
- `./gradlew :core:datastore:testDebugUnitTest`
- `./gradlew :core:datastore:lintDebug`
