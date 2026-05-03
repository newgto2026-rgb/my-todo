# :core:network 모듈 가이드

## 역할
- 네트워킹 기반(API 클라이언트, DTO 계약, 인터셉터).

## 규칙
- 전송 모델과 도메인 모델을 분리한다.
- 직렬화 계약은 명시적으로 유지한다.
- DTO는 서버/API 사정이 반영된 전송 모델이다. 서비스 의미를 담는 domain entity로 직접 사용하지 않는다.
- 네트워크 모듈은 API 계약, 직렬화, 인증/timeout/retry 같은 전송 관심사를 다루고 비즈니스 정책 판단을 하지 않는다.

## 변경 체크리스트
- API DTO 변경 시 `core:data` 매퍼를 함께 업데이트한다.
- timeout/retry/auth 동작은 중앙화된 정책으로 유지한다.

## 검증
- `./gradlew :core:network:lintDebug`
