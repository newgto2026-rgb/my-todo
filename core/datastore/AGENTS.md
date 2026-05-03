# :core:datastore 모듈 가이드

## 역할
- 환경설정 저장소와 사용자 설정 데이터 소스.

## 규칙
- 읽기/쓰기 API를 명확히 노출한다.
- 키 네이밍은 안정적으로 유지하고 마이그레이션을 고려한다.
- DataStore 값은 설정 저장 모델로 취급하고, 서비스 정책 판단을 직접 담지 않는다.
- preference 값을 domain에서 이해 가능한 값으로 매핑할 수 있지만, 화면 문구나 버튼 상태로 변환하지 않는다.

## 변경 체크리스트
- preference 키 변경 시 마이그레이션/호환 처리를 추가한다.
- 코루틴/플로우 스레딩 동작이 예측 가능해야 한다.

## 검증
- `./gradlew :core:datastore:testDebugUnitTest`
- `./gradlew :core:datastore:lintDebug`
