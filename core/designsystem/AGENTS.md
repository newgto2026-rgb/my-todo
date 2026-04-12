# :core:designsystem Module Guide

## Role
- App theme, typography, color tokens, shared visual primitives.

## Rules
- Centralize visual constants and theming decisions.
- Avoid feature-specific naming in shared tokens.

## Change Checklist
- Verify dark/light behavior and text contrast.
- Ensure changes do not silently regress downstream UI.

## Validate
- `./gradlew :core:designsystem:lintDebug`
