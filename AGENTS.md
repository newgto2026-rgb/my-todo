# Project Agent Guide (Indexed)

## Purpose
- Keep context small: read this file first, then only touched module guides.
- Module guides are delta-only; this root defines common defaults.

## Read Order
1. Read root `AGENTS.md`.
2. Identify touched Gradle module(s).
3. Open only those module `AGENTS.md` files.
4. For cross-module work, read touched modules only.

## Global Defaults
- Keep changes minimal, testable, and feature-focused.
- Add automated tests for behavior changes in the same PR.
- Maintain >=80% coverage for non-view layers (exclude Compose/View UI).
- Start code-change work in a new Git worktree by default (branch-isolated workspace per task/PR).
- Use a feature branch, never push feature work to `main`, always open a PR.
- Use descriptive commit messages and follow `.github/pull_request_template.md`.
- Respect module boundaries and dependency direction.
- Keep UI state-driven (`UiState + ViewModel + Action/SideEffect`), with side effects outside composables.
- Keep user-visible text in resources (`values`, `values-ko`).
- Keep DTO/transport, domain, and UI models separated with explicit mappers.
- Use `Result` or a standardized domain error model for failures.
- Use type-safe navigation contracts in `feature:*:api` when possible.
- Keep public API surfaces minimal; use `implementation` by default and `api` only for types intentionally exposed as module contracts.
- Never add `core:* -> feature:*` dependencies.
- Allow `feature:*:impl -> feature:*:api` only for cross-feature navigation contracts; avoid cross-feature reuse of implementation/business logic.
- Top tabs must map to implemented feature routes only; no placeholders in `:app`.
- Tab icons must be explicit icon assets (for example Material icons), not text glyphs.

## Tech Baseline
- Kotlin + Coroutines
- Jetpack Compose + Material 3
- Hilt DI
- Navigation Compose
- Room + DataStore
- Retrofit + OkHttp
- WorkManager

## Validation Defaults
- Per touched module: `./gradlew :<module>:lintDebug`
- For modules with unit tests: `./gradlew :<module>:testDebugUnitTest`
- App-level wiring/startup changes: `./gradlew :app:assembleDebug`
- Optional full checks: `./gradlew testDebugUnitTest`, `./gradlew connectedDebugAndroidTest`

## Worktree Rule
- Intent: isolate each coding task from the current working tree to reduce accidental cross-task changes and review noise.
- Trigger: any request that changes production/test code (not required for read-only analysis).
- Default flow:
1. Create a new branch and worktree for the task.
2. Do all edits/tests inside that worktree only.
3. Open PR from that branch/worktree.
- Allowed exceptions:
1. User explicitly requests in-place edits on the current worktree.
2. Docs-only or metadata-only updates where branch/worktree isolation is unnecessary.

### Command Template
- Variables:
  - `<task>`: short task slug (for example `todo-filter-bugfix`)
  - `<base>`: base branch (usually `main`)
- Create worktree + branch:
```bash
git fetch origin
git worktree add ../wt-<task> -b feat/<task> origin/<base>
```
- Enter and verify:
```bash
cd ../wt-<task>
git status
```
- After coding and test pass:
```bash
git add -A
git commit -m "feat(<scope>): <summary>"
git push -u origin feat/<task>
```
- Remove local worktree after merge/close:
```bash
cd /path/to/main/worktree
git worktree remove ../wt-<task>
git branch -d feat/<task>
```

## Module Index
| Gradle Module | Guide Path | Responsibility |
|---|---|---|
| `:app` | `app/AGENTS.md` | App shell and top-level wiring |
| `:core:model` | `core/model/AGENTS.md` | Shared immutable models |
| `:core:navigation` | `core/navigation/AGENTS.md` | Shared navigation contracts |
| `:core:domain` | `core/domain/AGENTS.md` | Use cases and repository contracts |
| `:core:data` | `core/data/AGENTS.md` | Repository implementations and mappers |
| `:core:database` | `core/database/AGENTS.md` | Room schema/DAO/migration |
| `:core:datastore` | `core/datastore/AGENTS.md` | Preference persistence |
| `:core:network` | `core/network/AGENTS.md` | API/DTO/network contracts |
| `:core:ui` | `core/ui/AGENTS.md` | Reusable UI components |
| `:core:designsystem` | `core/designsystem/AGENTS.md` | Theme and design tokens |
| `:core:testing` | `core/testing/AGENTS.md` | Shared testing helpers |
| `:feature:todo:api` | `feature/todo/api/AGENTS.md` | Todo public contracts/routes |
| `:feature:todo:impl` | `feature/todo/impl/AGENTS.md` | Todo UI + ViewModel logic |
| `:feature:todo:entry` | `feature/todo/entry/AGENTS.md` | Todo feature app wiring |
| `:feature:calendar:api` | `feature/calendar/api/AGENTS.md` | Calendar public contracts/routes |
| `:feature:calendar:impl` | `feature/calendar/impl/AGENTS.md` | Calendar UI + feature logic |
| `:feature:calendar:entry` | `feature/calendar/entry/AGENTS.md` | Calendar feature app wiring |

## Dependency Shape
- `app -> feature:*:api, feature:*:entry, core:*`
- `feature:*:entry -> feature:*:api, feature:*:impl`
- `feature:*:impl -> feature:*:api, core:*`
- `core:data -> core:domain + storage modules`
- `core:*` never depends on `feature:*`

## Boundary Clarifications
- `:app` is composition/root-runtime only (startup, root nav host, app-level workers/notifications/receivers).
- `:feature:*:entry` owns DI multibinding only; no screen/business logic.
- `:feature:*:impl` owns feature UI/ViewModel/use-case orchestration and navigation registration implementation.
- `:feature:*:api` owns only stable public contracts (routes/entry interfaces/contract models).
- `:core:navigation` owns app-wide navigation contracts shared across features.
