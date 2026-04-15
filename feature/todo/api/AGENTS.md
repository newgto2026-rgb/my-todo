# :feature:todo:api 모듈 가이드

## 역할
- 앱 및 다른 모듈에 노출되는 Todo 기능 공개 계약.
- 내비게이션 라우트/진입 계약.

## 규칙
- API 표면은 최소화하고 안정적으로 유지한다.
- `:feature:todo:impl`에 의존하면 안 된다.

## 변경 체크리스트
- 라우트/진입 계약 변경 시 `:feature:todo:impl`, `:feature:todo:entry`를 함께 업데이트한다.
- 타입 안전 라우트 정의를 우선한다.

## 검증
- `./gradlew :feature:todo:api:lintDebug`
