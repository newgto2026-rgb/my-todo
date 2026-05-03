# :feature:calendar:api 모듈 가이드

## 역할
- Calendar 기능 내비게이션용 공개 계약/라우트.

## 규칙
- 라우트 계약은 타입 안전하고 안정적으로 유지한다.
- 모듈에 구현 세부 사항을 포함하지 않는다.
- 공개 계약에는 Calendar 진입/라우트 의미만 둔다.
- 날짜 필터링, 일정 노출 정책, 화면 상태 변환은 impl/domain/use case 책임으로 유지한다.

## 검증
- `./gradlew :feature:calendar:api:assembleDebug`
