# :core:datastore 모듈 가이드

## 역할
- 환경설정 저장소와 사용자 설정 데이터 소스.

## 규칙
- 읽기/쓰기 API를 명확히 노출한다.
- 키 네이밍은 안정적으로 유지하고 마이그레이션을 고려한다.

## 변경 체크리스트
- preference 키 변경 시 마이그레이션/호환 처리를 추가한다.
- 코루틴/플로우 스레딩 동작이 예측 가능해야 한다.

## 검증
- `./gradlew :core:datastore:testDebugUnitTest`
- `./gradlew :core:datastore:lintDebug`
