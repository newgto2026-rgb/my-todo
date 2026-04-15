# :core:testing 모듈 가이드

## 역할
- 공용 테스트 규칙, fake, 유틸리티.

## 규칙
- 테스트 헬퍼는 결정적이고 가볍게 유지한다.
- fake가 특정 기능 구현 하나에 과도하게 결합되지 않게 한다.

## 변경 체크리스트
- 리포지토리 계약이 바뀌면 공용 fake를 먼저 업데이트한다.
- dispatcher/시간 제어 유틸리티는 명시적으로 유지한다.

## 검증
- `./gradlew :core:testing:testDebugUnitTest`
