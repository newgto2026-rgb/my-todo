# :core:datastore Module Delta

## Scope
- Preference persistence and user setting data sources.

## Module Rules
- Keep read/write APIs explicit and stable.
- Keep key names migration-safe.
- If keys change, add compatibility or migration handling.
- Keep coroutine/Flow threading behavior predictable.
