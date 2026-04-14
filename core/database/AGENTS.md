# :core:database Module Delta

## Scope
- Room database, entities, DAO, and migrations.

## Module Rules
- Never break existing schema without migration.
- Keep DAO queries deterministic and test-covered.
- When schema changes, update `schemas/` snapshots and migration tests.
