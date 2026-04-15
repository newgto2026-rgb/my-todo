# PRD 정책

모든 신규 구현 작업은 PRD와 연결되어야 한다.

## 네이밍
- 브랜치: `feat/PRD-123-...` 또는 `fix/PRD-123-...`
- PRD 파일: `docs/prd/PRD-123.md`
- 커밋 메시지: `[PRD-123]` 포함

## 필수 프런트매터
```yaml
prd_id: PRD-123
status: approved
owner: @owner
last_updated: 2026-04-15
scope_modules:
  - feature/todo/impl
```
