# :app Module Guide

## Role
- Application shell and startup composition.
- Top-level navigation host and Android entry points.
- Runtime integration (notifications, workers, receivers).

## Owns
- `MainActivity`, `AppNavHost`, `Application` wiring.
- AndroidManifest app-level declarations.
- App-level resources and notification text.

## Boundaries
- Depend on `feature:*:api`, `feature:*:entry`, and `core:*`.
- Do not implement feature internals directly.

## Change Checklist
- If navigation/start destination changes, verify feature-entry contract wiring.
- Keep app resources locale-safe (`values`, `values-ko`).
- For reminders/worker changes, validate runtime permissions and channel behavior.

## Validate
- `./gradlew :app:assembleDebug`
- `./gradlew :app:lintDebug`
