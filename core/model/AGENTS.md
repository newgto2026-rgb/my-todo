# :core:model Module Delta

## Scope
- Pure Kotlin models shared across layers.

## Module Rules
- No Android framework dependency.
- Keep models immutable and serialization-friendly.
- Avoid behavior-heavy logic in this module.
- If enum/type changes, verify dependent mappers and use cases.
