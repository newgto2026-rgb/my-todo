# :core:network 모듈 가이드

## 역할
- 네트워킹 기반(API 클라이언트, DTO 계약, 인터셉터).

## 규칙
- 전송 모델과 도메인 모델을 분리한다.
- 직렬화 계약은 명시적으로 유지한다.

## 변경 체크리스트
- API DTO 변경 시 `core:data` 매퍼를 함께 업데이트한다.
- timeout/retry/auth 동작은 중앙화된 정책으로 유지한다.

## 검증
- `./gradlew :core:network:lintDebug`
