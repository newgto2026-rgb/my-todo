# CI Setup Report

## Date
- 2026-04-12

## Scope
- Configure GitHub Actions CI to run automatically when code is pushed or when pull requests are opened/updated.
- Leave a detailed record of what was configured and why.

## What Was Added
- Added workflow file:
  - `.github/workflows/android-ci.yml`

## Workflow Configuration

### Workflow Name
- `Android CI`

### Triggers
- `push` on:
  - `main`
  - `develop`
- `pull_request` on:
  - `main`
  - `develop`
- `workflow_dispatch` for manual runs from GitHub UI

### Concurrency
- Enabled:
  - `group: android-ci-${{ github.ref }}`
  - `cancel-in-progress: true`
- Purpose:
  - Cancel older running CI for the same branch/ref and keep only the latest run active.

### Permissions
- Minimal permission model:
  - `contents: read`
- Purpose:
  - Follow least-privilege defaults for CI.

### Runner
- `ubuntu-latest`
- Timeout:
  - `30` minutes

### CI Steps
1. Checkout source (`actions/checkout@v4`)
2. Setup Java 17 (`actions/setup-java@v4`, Temurin)
3. Setup Gradle with cache (`gradle/actions/setup-gradle@v4`)
4. Ensure gradlew is executable
5. Run unit tests:
   - `./gradlew --stacktrace testDebugUnitTest`
6. Build debug artifact:
   - `./gradlew --stacktrace assembleDebug`
7. Run static analysis:
   - `./gradlew --stacktrace lintDebug`
8. Upload artifacts (always):
   - Unit test results and reports
   - Lint reports
   - General build reports/logs

## Artifacts Uploaded
- `test-reports`
  - `**/build/test-results/**`
  - `**/build/reports/tests/**`
- `lint-reports`
  - `**/build/reports/lint-results*.html`
  - `**/build/reports/lint-results*.xml`
- `build-reports`
  - `app/build/reports/**`
  - `**/build/outputs/logs/**`

## Expected Behavior
- Any push to `main` or `develop` starts CI automatically.
- Any pull request targeting `main` or `develop` starts CI automatically.
- Failures include stacktraces and downloadable reports for diagnosis.

## Recommended GitHub Repository Settings
- Enable branch protection for `main`:
  - Require pull request before merging
  - Require status checks to pass before merging
  - Select check: `Build, Unit Test, Lint`
- Optional:
  - Apply same rules to `develop`
  - Require linear history
  - Dismiss stale approvals when new commits are pushed

## Notes
- This setup does not run instrumentation tests (`connectedDebugAndroidTest`) because emulator jobs are slower and usually split into a dedicated workflow.
- Existing local uncommitted IDE files were intentionally left untouched.
