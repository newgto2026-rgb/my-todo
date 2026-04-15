# PRD Policy

모든 신규 구현 작업은 PRD와 연결되어야 합니다.

## Naming
- Branch: `feat/PRD-123-...` 또는 `fix/PRD-123-...`
- PRD file: `docs/prd/PRD-123.md`
- Commit message: `[PRD-123]` 포함

## Required front matter
```yaml
prd_id: PRD-123
status: approved
owner: @owner
last_updated: 2026-04-15
scope_modules:
  - feature/todo/impl
```
