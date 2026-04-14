# :feature:todo:impl Module Delta

## Scope
- Todo feature implementation: composables, ViewModel, and state transitions.

## Module Rules
- Keep screen flow `UiState + Action + SideEffect + ViewModel`.
- Keep side effects in ViewModel/use case layers.
- Keep user-visible text in resources.
- Keep navigation contracts in `:feature:todo:api`.
- Cross-feature dependencies are allowed only to other feature `:api` modules for navigation contracts.

## UI Folder Convention
- `ui/screen/`: route and screen entry composition.
- `ui/components/`: reusable child views.
- `ui/editor/`: editor bottom-sheet sections and date-time helpers.
- `ui/sheet/`: non-editor modal sheets.
- Keep `UiState`/`Action`/`SideEffect`/`ViewModel` and mapper/validator in `ui/` root.
