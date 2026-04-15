# :core:domain 모듈 가이드

## 역할
- 유스케이스/인터랙터와 리포지토리 계약.
- 도메인 스케줄러 계약.

## 규칙
- Android UI 프레임워크 의존 금지.
- 유스케이스는 작고 조합 가능하게 유지.
- 실패는 `Result` 또는 도메인 에러 모델로 표현.
- 리포지토리 계약은 관심사별로 분리해 유지:
  - `TodoItemRepository`, `TodoCategoryRepository`, `TodoFilterRepository`, `TodoReminderRepository`.

## 변경 체크리스트
- 리포지토리 계약 변경 시 `core:data` 구현을 함께 업데이트한다.
- 유스케이스 이름은 동작 중심으로 짓고 테스트를 먼저 작성한다.

## 검증
- `./gradlew :core:domain:testDebugUnitTest`
