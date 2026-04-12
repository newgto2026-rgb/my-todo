# :core:model Module Guide

## Role
- Pure Kotlin model definitions shared across layers.

## Rules
- No Android framework dependency.
- Keep models immutable and serialization-friendly.
- Avoid behavior-heavy logic here.

## Change Checklist
- Confirm backward compatibility for fields used by DB/data/domain/UI.
- If enum/type changes, verify mappers and use cases in dependent modules.

## Validate
- `./gradlew :core:model:testDebugUnitTest`
