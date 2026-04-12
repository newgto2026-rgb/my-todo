# :core:ui Module Guide

## Role
- Reusable UI components shared by multiple features.

## Rules
- Keep components stateless where possible.
- No feature-specific business logic.
- Follow design-system tokens/styles.

## Change Checklist
- Preserve binary compatibility for widely used composables.
- Check accessibility labels and test tags when relevant.

## Validate
- `./gradlew :core:ui:lintDebug`
