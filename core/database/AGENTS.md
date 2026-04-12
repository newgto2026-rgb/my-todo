# :core:database Module Guide

## Role
- Room database, entities, DAO, migrations.

## Rules
- Migration safety first; never break existing schema without migration.
- Keep DAO queries deterministic and test-covered.

## Change Checklist
- Update `schemas/` snapshots when schema changes.
- Add/adjust migration tests for version bumps.

## Validate
- `./gradlew :core:database:testDebugUnitTest`
- `./gradlew :core:database:lintDebug`
