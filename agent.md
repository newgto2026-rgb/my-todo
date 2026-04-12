# Project Agent Guide

## Goal
- This is a greenfield Android app.
- Follow modern Android architecture with clear module boundaries.
- Keep changes small, testable, and feature-focused.

## Tech Stack
- Language: Kotlin
- UI: Jetpack Compose + Material 3
- Architecture: UDF (StateFlow/Flow + ViewModel)
- DI: Hilt
- Navigation: Navigation Compose (type-safe routes if available)
- Data:
  - Local: Room, DataStore
  - Remote: Retrofit, OkHttp
- Background: WorkManager
- Async: Kotlin Coroutines
- Build: Gradle Kotlin DSL

## Monorepo Layout
- `app/` : application shell, top-level navigation, app-wide DI
- `core/` : shared foundations
  - `core:model` : domain/data models
  - `core:ui` : reusable UI components
  - `core:designsystem` : theme, typography, components
  - `core:network` : API + DTO + networking setup
  - `core:database` : Room entities/dao/db
  - `core:datastore` : preferences/proto store
  - `core:data` : repositories and mappers
  - `core:domain` : use cases/interactors
  - `core:testing` : test fakes/utilities/rules
- `feature/` : vertical features by domain
  - Preferred pattern: `feature:<name>:api` + `feature:<name>:impl`
  - Example: `feature:home:api`, `feature:home:impl`

## Module Rules
- `feature:*:api`
  - Navigation contracts, public feature entry points, minimal interfaces
  - Must not depend on `feature:*:impl`
- `feature:*:impl`
  - Screen UI, ViewModel, feature-specific logic
  - Can depend on `core:*` and corresponding `feature:*:api`
- `core:*`
  - No dependency on `feature:*`
- Dependency direction should always point inward to shared/core/domain layers.

## Coding Conventions
- One screen = one `UiState` + one `ViewModel` + one route/entry provider.
- UI must be state-driven; avoid direct mutable UI logic in composables.
- Keep side effects in ViewModel/use cases.
- Prefer immutable models and explicit mapper functions.
- Use `Result`/sealed state for loading-success-error states.

## Testing Strategy
- Unit tests first for ViewModel and use cases.
- UI tests for key user flows in feature modules.
- Use fake repositories in local tests.
- Avoid brittle tests tied to implementation details.

## Build/Test Commands
- Build debug app: `./gradlew assembleDebug`
- Run unit tests: `./gradlew testDebugUnitTest`
- Run single test: `./gradlew testDebugUnitTest --tests "com.example.MyTest"`
- Run instrumentation tests: `./gradlew connectedDebugAndroidTest`
- Lint + format: `./gradlew spotlessApply` (or project formatter task)

## Agent Working Rules
- Before editing, identify target module and dependency impact.
- Prefer minimal diffs and keep public contracts stable.
- If adding a feature, create `api` first, then `impl`.
- If touching data flow, verify:
  1) repository contract
  2) use case behavior
  3) ViewModel state mapping
  4) UI rendering state
- Always list changed modules in summary.

## Token-Saving Defaults
- Keep responses short: summary + minimal diff.
- Read only relevant files, not full-tree scans.
- Share only relevant logs (error lines + nearby context).
- Execute one task at a time when possible.

## Additional Modern Practices

- UI State
  - Use a single immutable UiState data class per screen.
  - Handle one-off events via SideEffect (SharedFlow).

- Pagination
  - Use Paging 3 for all paginated lists by default.

- Serialization
  - Prefer kotlinx.serialization over Gson.

- Navigation
  - Prefer type-safe navigation (Kotlin serialization or codegen).

- Build
  - Use Gradle convention plugins (build-logic module).

- Performance
  - Maintain Baseline Profiles for startup and scrolling performance.

- Compose Stability
  - Use @Stable / @Immutable where applicable.
  - Avoid unnecessary recomposition.

- Testing
  - Use Turbine for Flow testing.
  - Standardize Compose UI test patterns.

- Error Handling
  - Standardize error models across data/domain layers.

- Observability
  - Use structured logging and crash reporting tools.
