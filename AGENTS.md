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
- Respect module boundaries and dependency direction.
- Prefer UDF on UI screens: `UiState + ViewModel + Action/SideEffect`.
- Put user-visible strings in resources (`values`, `values-ko`).
- Use type-safe navigation contracts in `feature:*:api` when possible.
- Do not add `core:* -> feature:*` dependencies.

## Tech Baseline
- Kotlin + Coroutines
- Jetpack Compose + Material 3
- Hilt DI
- Navigation Compose
- Room + DataStore
- WorkManager

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

## Current Dependency Shape
- `app -> feature:*:api, feature:*:entry, core:*`
- `feature:*:entry -> feature:*:api, feature:*:impl`
- `feature:*:impl -> feature:*:api, core:*`
- `core:data -> core:domain + storage modules`
- `core:*` never depends on `feature:*`

## Common Commands
- Build app: `./gradlew assembleDebug`
- Unit tests: `./gradlew testDebugUnitTest`
- Module unit tests: `./gradlew :<module>:testDebugUnitTest`
- Module lint: `./gradlew :<module>:lintDebug`
