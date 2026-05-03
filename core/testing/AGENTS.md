# :core:testing 모듈 가이드

## 역할
- 공용 테스트 규칙, fake, 유틸리티.

## 규칙
- 테스트 헬퍼는 결정적이고 가볍게 유지한다.
- fake가 특정 기능 구현 하나에 과도하게 결합되지 않게 한다.
- 비즈니스 로직 테스트는 domain/use case/entity 규칙을 직접 검증할 수 있게 fake repository와 시간 제어 도구를 제공한다.
- 프레젠테이션 로직 테스트는 ViewModel/UiModel 변환 결과와 SideEffect를 검증할 수 있게 공용 rule/helper를 제공한다.
- fake repository에는 테스트 데이터 제공 로직만 두고, 실제 서비스 정책을 중복 구현하지 않는다.

## 변경 체크리스트
- 리포지토리 계약이 바뀌면 공용 fake를 먼저 업데이트한다.
- dispatcher/시간 제어 유틸리티는 명시적으로 유지한다.

## 검증
- `./gradlew :core:testing:testDebugUnitTest`
