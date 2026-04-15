# PRD 문서 가이드

`docs/prd`는 요구사항 문서를 관리하기 위한 선택형 문서 저장소다.

## 권장 네이밍
- PRD 파일: `docs/prd/PRD-123.md`
- 문서 내부 `prd_id` 값과 파일명을 일치시킨다.

## 필수 프런트매터
```yaml
prd_id: PRD-123
status: approved
owner: @owner
last_updated: 2026-04-15
scope_modules:
  - feature/todo/impl
```
