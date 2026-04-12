# :feature:todo:impl Module Guide

## Role
- Todo feature implementation: composables, ViewModel, state transitions.

## Rules
- One screen flow should remain `UiState + Action + SideEffect + ViewModel`.
- Keep side effects in ViewModel/use case layers.
- User-visible text must come from resources.

## Change Checklist
- Verify state mapping chain:
  1) repository/use case output
  2) UI mapper
  3) ViewModel state + side effects
  4) composable rendering
- If adding navigation behavior, keep contract in `:feature:todo:api`.

## Validate
- `./gradlew :feature:todo:impl:testDebugUnitTest`
- `./gradlew :feature:todo:impl:lintDebug`
