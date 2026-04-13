# :feature:calendar:entry Module Guide

## Role
- Hilt multibinding entry point registration for calendar feature.

## Rules
- Bind only via `AppFeatureEntry` set multibinding.
- Keep `app` module decoupled from `:feature:calendar:impl`.

## Validate
- `./gradlew :feature:calendar:entry:assembleDebug`
