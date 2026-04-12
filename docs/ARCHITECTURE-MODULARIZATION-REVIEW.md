# Architecture / Modularization Review

## Date
- 2026-04-12

## Scope
- Review current modularization quality for `app`, `core:*`, `feature:*`.
- Identify gaps and propose concrete improvement priorities.
- Execute priority 1~2 immediately in a dedicated branch/PR.

## Current Strengths
- `feature:todo:api` + `feature:todo:impl` split exists.
- Screen architecture follows UDF style (`UiState`, `Action`, `SideEffect`, `ViewModel`).
- Compose UI is already decomposed into smaller components (header/filter/bottom sheet/list row).

## Main Gaps Found
1. `feature:todo:api` was only a typealias and not an explicit feature contract.
2. `app` directly depended on `feature:todo:impl`.
3. App start destination logic contained feature-specific route constant.
4. Navigation route constants were duplicated across app and feature impl.
5. Many UI strings are hardcoded in Kotlin instead of resources.
6. `TodoRepository` currently owns multiple concerns (todo/category/filter preferences).
7. Reminder domain is split between standalone reminder model and todo-embedded reminder fields.

## Priority Plan
### P1. Strengthen Feature API Contract
- Replace typealias with explicit `TodoFeatureEntry` contract interface in `feature:todo:api`.
- Centralize todo route constant in the API contract.

### P2. Reduce App -> Feature Impl Direct Coupling
- Introduce `feature:todo:entry` module to host wiring from impl to app-level entry set.
- Move Hilt multibinding module out of impl into entry module.
- Switch app dependency from `:feature:todo:impl` to `:feature:todo:entry`.
- Remove app hardcoded todo route and select start destination through entry contract flag.

## Executed Changes (P1~P2)
- Added explicit API contract:
  - `feature/todo/api/src/main/java/com/example/myfirstapp/feature/todo/api/TodoFeatureEntry.kt`
- Added generic start destination flag to app feature contract:
  - `core/ui/src/main/java/com/example/myfirstapp/core/ui/navigation/AppFeatureEntry.kt`
- Updated todo feature entry implementation to use API contract + start destination flag:
  - `feature/todo/impl/src/main/java/com/example/myfirstapp/feature/todo/impl/navigation/TodoFeatureEntryImpl.kt`
- Added new wiring module:
  - `feature/todo/entry/build.gradle.kts`
  - `feature/todo/entry/src/main/AndroidManifest.xml`
  - `feature/todo/entry/src/main/java/com/example/myfirstapp/feature/todo/entry/di/TodoFeatureEntryModule.kt`
- Removed old impl-local binding module:
  - `feature/todo/impl/src/main/java/com/example/myfirstapp/feature/todo/impl/di/TodoFeatureModule.kt`
- Updated module graph:
  - `settings.gradle.kts`
  - `app/build.gradle.kts`
- Removed app hardcoded route dependency:
  - `app/src/main/java/com/example/myfirstapp/app/AppNavHost.kt`

## Next Recommended Steps (Not in this change)
1. Split `TodoRepository` into smaller repository contracts by responsibility.
2. Consolidate reminder domain model direction (standalone vs embedded in todo).
3. Move user-facing strings to `strings.xml` and support localization.
4. Introduce type-safe navigation route model instead of raw strings.

