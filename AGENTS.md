# Project Agent Guide (Indexed)

## Purpose
- Keep agent context small and accurate by reading only relevant guides.
- Use this file as the entry index, then open only target module guides.

## How To Use This Index
1. Read this root guide first.
2. Identify target Gradle module(s).
3. Open only `AGENTS.md` for those module(s).
4. For cross-module changes, read all touched modules only.

## Global Rules
- Keep changes minimal, testable, and feature-focused.
- Before any implementation work, create and use a separate Git worktree on a dedicated branch (never work directly in the primary checkout).
- Before starting a new implementation task, a matching PRD must exist under `docs/prd` and be linked by PRD ID in branch/commit/PR.
- Every implementation change must include automated tests (unit/integration) in the same PR.
- After implementation, verify test coverage is at least 80% for non-view layers (exclude Compose/View UI code).
- When pushing changes, always use a separate branch (never push feature work directly to `main`).
- Always open a PR when pushing code.
- Always use organized, descriptive commit messages for PR commits.
- Respect module boundaries and dependency direction.
- Prefer UDF on UI screens: `UiState + ViewModel + Action/SideEffect`.
- Put user-visible strings in resources (`values`, `values-ko`).
- Use type-safe navigation contracts in `feature:*:api` when possible.
- Do not add `core:* -> feature:*` dependencies.
- Top-level tabs must map to implemented feature routes only; do not add placeholder tabs/screens in `:app`.
- Tab icons must use explicit icon assets (e.g., Material icons), not text glyph substitutes.

## Tech Baseline
- Kotlin + Coroutines
- Jetpack Compose + Material 3
- Hilt DI
- Navigation Compose
- Room + DataStore
- Retrofit + OkHttp
- WorkManager

## Engineering Defaults
- Architecture: Keep UI strictly state-driven and immutable (`UiState` 중심), one-off events via `SideEffect`.
- Data/Network: Prefer explicit mapper/contract boundaries; use `kotlinx.serialization` for serialization.
- Testing: Prioritize unit tests for ViewModel/use case first, use fakes in local tests, and avoid implementation-coupled brittle tests.
- Quality: Keep public contracts stable and list touched modules in work summaries.
- Performance/Compose: Minimize unnecessary recomposition and apply stability annotations (`@Stable`, `@Immutable`) where valid.

## Architecture Conventions
- Keep side effects in ViewModel/use case layers, not inside composables.
- Prefer immutable models and explicit mapper functions between layer boundaries.
- Use `Result` or standardized error model for failure paths across data/domain.

## Data And API Conventions
- For paginated lists, use Paging 3 by default.
- Keep transport/DTO models separate from domain models.
- Keep repository contracts focused by concern and avoid feature leakage into core layers.
- Keep preference/database keys and schema evolution migration-safe.

## Testing And Quality Gates
- Prioritize unit tests for use cases and ViewModels; add UI tests for key user flows.
- Use Turbine for Flow testing and shared fakes/rules from `core:testing`.
- Keep tests meaningful and behavior-oriented rather than implementation-coupled.

## Build And Operations
- Prefer Gradle convention-plugin style build organization (`build-logic`) when build complexity grows.
- Keep lint and test tasks runnable per module and at repo root.
- Use structured logging and crash-reporting-ready error context for operational visibility.

## PR Description Standard
- Every PR description must follow `.github/pull_request_template.md`.

## Module Index
| Gradle Module | Guide Path | Responsibility |
|---|---|---|
| `:app` | `app/AGENTS.md` | App shell, startup, top-level wiring |
| `:core:model` | `core/model/AGENTS.md` | Pure domain/data models |
| `:core:domain` | `core/domain/AGENTS.md` | Use cases + repository contracts |
| `:core:data` | `core/data/AGENTS.md` | Repository implementations + mappers |
| `:core:database` | `core/database/AGENTS.md` | Room DB/entities/dao/migrations |
| `:core:datastore` | `core/datastore/AGENTS.md` | Preference data source layer |
| `:core:network` | `core/network/AGENTS.md` | Remote API/networking contracts |
| `:core:ui` | `core/ui/AGENTS.md` | Reusable UI primitives |
| `:core:designsystem` | `core/designsystem/AGENTS.md` | Theme, typography, design tokens |
| `:core:testing` | `core/testing/AGENTS.md` | Shared test fakes/rules/helpers |
| `:feature:todo:api` | `feature/todo/api/AGENTS.md` | Todo public contracts/routes |
| `:feature:todo:impl` | `feature/todo/impl/AGENTS.md` | Todo UI + ViewModel + feature logic |
| `:feature:todo:entry` | `feature/todo/entry/AGENTS.md` | App wiring/bindings for todo entry |
| `:feature:calendar:api` | `feature/calendar/api/AGENTS.md` | Calendar public contracts/routes |
| `:feature:calendar:impl` | `feature/calendar/impl/AGENTS.md` | Calendar UI + feature logic |
| `:feature:calendar:entry` | `feature/calendar/entry/AGENTS.md` | App wiring/bindings for calendar entry |

## Current Dependency Shape
- `app -> feature:*:api, feature:*:entry, core:*`
- `feature:*:entry -> feature:*:api, feature:*:impl`
- `feature:*:impl -> feature:*:api, core:*`
- `core:data -> core:domain + storage modules`
- `core:*` never depends on `feature:*`

## Common Commands
- Build app: `./gradlew assembleDebug`
- Unit tests: `./gradlew testDebugUnitTest`
- Single test: `./gradlew testDebugUnitTest --tests "com.example.MyTest"`
- Instrumentation tests: `./gradlew connectedDebugAndroidTest`
- Module unit tests: `./gradlew :<module>:testDebugUnitTest`
- Module lint: `./gradlew :<module>:lintDebug`
