# :core:database 모듈 가이드

## 역할
- Room 데이터베이스, 엔티티, DAO, 마이그레이션.

## 규칙
- 마이그레이션 안전성을 최우선으로 두고, 마이그레이션 없는 스키마 파괴 변경을 금지한다.
- DAO 쿼리는 결정적 동작을 유지하고 테스트로 검증한다.
- Room `Entity`는 저장 구조를 표현하는 persistence model이며, 서비스 의미를 표현하는 domain entity와 구분한다.
- DAO/DB Entity에는 저장/조회에 필요한 데이터 로직만 둔다. 취소 가능 여부, 배송 가능 여부 같은 비즈니스 판단은 domain/use case에서 처리한다.

## 변경 체크리스트
- 스키마가 바뀌면 `schemas/` 스냅샷을 갱신한다.
- 버전 상승 시 마이그레이션 테스트를 추가/수정한다.

## 검증
- `./gradlew :core:database:testDebugUnitTest`
- `./gradlew :core:database:lintDebug`
