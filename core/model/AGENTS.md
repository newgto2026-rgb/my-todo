# :core:model 모듈 가이드

## 역할
- 레이어 전반에서 공유되는 순수 Kotlin 모델 정의.

## 규칙
- Android 프레임워크 의존 금지.
- 모델은 불변성과 직렬화 친화성을 유지한다.
- 동작 중심의 무거운 로직은 두지 않는다.

## 변경 체크리스트
- DB/data/domain/UI에서 사용하는 필드의 하위 호환성을 확인한다.
- enum/type 변경 시 의존 모듈의 매퍼와 유스케이스를 검증한다.

## 검증
- `./gradlew :core:model:testDebugUnitTest`
