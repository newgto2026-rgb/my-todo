# :core:testing Module Guide

## Role
- Shared test rules, fakes, and utilities.

## Rules
- Keep test helpers deterministic and lightweight.
- Do not couple fakes tightly to one feature implementation.

## Change Checklist
- If repository contracts evolve, update shared fakes first.
- Keep dispatcher/time control utilities explicit.

## Validate
- `./gradlew :core:testing:testDebugUnitTest`
