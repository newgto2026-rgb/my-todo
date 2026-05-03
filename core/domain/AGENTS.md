# :core:domain 모듈 가이드

## 역할
- 유스케이스/인터랙터와 리포지토리 계약.
- 도메인 스케줄러 계약.

## 규칙
- Android UI 프레임워크 의존 금지.
- 유스케이스는 작고 조합 가능하게 유지.
- 유스케이스는 기능 수행 절차를 조율하는 애플리케이션 로직을 담당한다.
- 유스케이스는 필요한 repository 데이터를 가져오고 조합한 뒤 도메인 규칙을 실행한다.
- 취소/배송/완료 가능 여부, 상태 전이, 정책 계산은 비즈니스 로직으로 보고 entity/domain service/use case에 둔다.
- 화면 문구, 버튼 활성화 문구, 색상, 토스트 텍스트 같은 프레젠테이션 결정은 반환하지 않는다.
- 실패는 `Result` 또는 도메인 에러 모델로 표현.
- 리포지토리 계약은 관심사별로 분리해 유지:
  - `TodoItemRepository`, `TodoCategoryRepository`, `TodoFilterRepository`, `TodoReminderRepository`.

## 변경 체크리스트
- 리포지토리 계약 변경 시 `core:data` 구현을 함께 업데이트한다.
- 유스케이스 이름은 동작 중심으로 짓고 테스트를 먼저 작성한다.

## 검증
- `./gradlew :core:domain:testDebugUnitTest`
