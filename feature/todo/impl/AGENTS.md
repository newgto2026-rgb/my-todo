# :feature:todo:impl Module Guide

## Role
- Todo feature implementation: composables, ViewModel, state transitions.

## Rules
- One screen flow should remain `UiState + Action + SideEffect + ViewModel`.
- Keep side effects in ViewModel/use case layers.
- User-visible text must come from resources.

## UI Folder Convention
- `ui/screen/`: route + screen entry composition (`TodoListRoute`, screen-level wiring).
- `ui/components/`: reusable screen child views (header/filter/empty state).
- `ui/editor/`: todo editor bottom-sheet sections and date-time editor helpers.
- `ui/sheet/`: non-editor modal sheets (category manager, etc.).
- Keep state logic files (`UiState`, `Action`, `SideEffect`, `ViewModel`, mapper/validator) in `ui/` root.

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
