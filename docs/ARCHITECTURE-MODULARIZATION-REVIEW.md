# Architecture / Modularization Review

## Date
- 2026-04-12

## Scope
- Review current modularization quality for `app`, `core:*`, `feature:*`.
- Identify gaps and propose concrete improvement priorities.
- Execute priority 1~4 in phased branches/PRs.

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

## Executed Changes (P3~P4)
- P3. User-facing string resource migration
  - Added todo feature string resources:
    - `feature/todo/impl/src/main/res/values/strings.xml`
  - Replaced hardcoded UI labels/messages in:
    - `feature/todo/impl/src/main/java/com/example/myfirstapp/feature/todo/impl/ui/TodoListScreen.kt`
    - `feature/todo/impl/src/main/java/com/example/myfirstapp/feature/todo/impl/ui/TodoFilterBar.kt`
    - `feature/todo/impl/src/main/java/com/example/myfirstapp/feature/todo/impl/ui/TodoCategoryFilterBar.kt`
    - `feature/todo/impl/src/main/java/com/example/myfirstapp/feature/todo/impl/ui/TodoEditorCategorySection.kt`
    - `feature/todo/impl/src/main/java/com/example/myfirstapp/feature/todo/impl/ui/TodoEditorReminderSection.kt`
    - `feature/todo/impl/src/main/java/com/example/myfirstapp/feature/todo/impl/ui/TodoEditBottomSheet.kt`
    - `feature/todo/impl/src/main/java/com/example/myfirstapp/feature/todo/impl/ui/TodoHeader.kt`
    - `feature/todo/impl/src/main/java/com/example/myfirstapp/feature/todo/impl/ui/TodoEmptyState.kt`
    - `feature/todo/impl/src/main/java/com/example/myfirstapp/feature/todo/impl/ui/CategoryManagerBottomSheet.kt`
  - Converted validation/snackbar messages from raw strings to `@StringRes` ids:
    - `feature/todo/impl/src/main/java/com/example/myfirstapp/feature/todo/impl/ui/TodoListInputValidator.kt`
    - `feature/todo/impl/src/main/java/com/example/myfirstapp/feature/todo/impl/ui/TodoListSideEffect.kt`
    - `feature/todo/impl/src/main/java/com/example/myfirstapp/feature/todo/impl/ui/TodoListUiState.kt`
    - `feature/todo/impl/src/main/java/com/example/myfirstapp/feature/todo/impl/ui/TodoListViewModel.kt`
    - `feature/todo/impl/src/test/java/com/example/myfirstapp/feature/todo/impl/ui/TodoListViewModelTest.kt`
  - Added app-level reminder notification title resource:
    - `app/src/main/res/values/strings.xml`
    - `app/src/main/java/com/example/myfirstapp/app/todo/reminder/TodoReminderNotificationHelper.kt`

- P4. Type-safe navigation adoption for todo feature entry
  - Added serializable typed route contract:
    - `feature/todo/api/src/main/java/com/example/myfirstapp/feature/todo/api/TodoFeatureEntry.kt`
  - Switched todo feature registration to `composable<TodoRoute>`:
    - `feature/todo/impl/src/main/java/com/example/myfirstapp/feature/todo/impl/navigation/TodoFeatureEntryImpl.kt`
  - Enabled Kotlin serialization plugin/dependency for route contract module:
    - `feature/todo/api/build.gradle.kts`
    - `gradle/libs.versions.toml` (`kotlinx.serialization` version aligned with Kotlin 2.0.21)

## Verification
- Unit test:
  - `./gradlew :feature:todo:impl:testDebugUnitTest` (PASS)
- Lint:
  - `./gradlew :feature:todo:api:lintDebug :feature:todo:impl:lintDebug :app:lintDebug` (PASS)

## Executed Changes (P5~P6)
- P5. Repository contract split by responsibility
  - Replaced monolithic `TodoRepository` with focused domain contracts:
    - `TodoItemRepository`
    - `TodoCategoryRepository`
    - `TodoFilterRepository`
    - `TodoReminderRepository`
  - Updated all todo/category/filter use cases to inject the new focused contracts.
  - Updated data binding module to bind one implementation (`TodoRepositoryImpl`) to all focused contracts.
  - Updated test fakes and feature tests to implement/use the split contracts.

- P6. Todo reminder domain direction consolidation
  - Clarified todo-embedded reminder access through dedicated contract/use case path:
    - `TodoReminderRepository`
    - `GetActiveTodoRemindersUseCase` (renamed from `GetTodosWithActiveReminderUseCase`)
  - Updated app scheduler wiring to use `GetActiveTodoRemindersUseCase`.
  - Result: standalone reminder domain (`ReminderRepository`) remains for generic reminders, while todo reminder scheduling reads from explicit todo reminder contract.

## Verification (P5~P6)
- Unit tests:
  - `./gradlew :core:domain:test` (PASS)
  - `./gradlew :core:data:testDebugUnitTest` (PASS)
  - `./gradlew :feature:todo:impl:testDebugUnitTest` (PASS)
- Build:
  - `./gradlew :app:assembleDebug` (PASS)
