# :feature:calendar:impl Module Delta

## Scope
- Calendar feature implementation: screen composition, ViewModel/state logic, and feature navigation registration implementation.

## Module Rules
- Keep screen logic state-driven (`UiState + Action + SideEffect + ViewModel`).
- Keep user-visible text in resources.
- Cross-feature dependencies are allowed only to other feature `:api` modules for navigation contracts.
