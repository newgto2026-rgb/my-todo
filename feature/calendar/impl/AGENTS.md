# :feature:calendar:impl 모듈 가이드

## 역할
- Calendar 기능 구현: 화면 구성과 기능 진입 연결.

## 규칙
- 화면은 상태 중심으로 유지하고 `UiState + Action + SideEffect` 확장 가능 구조를 준비한다.
- 사용자 노출 텍스트는 리소스로 관리한다.
- `ui/screen`은 화면 진입/조립 역할만 유지하고, 세부 뷰는 기능 단위로 `ui/components`에 분리한다.

## 검증
- `./gradlew :feature:calendar:impl:testDebugUnitTest`
- `./gradlew :feature:calendar:impl:lintDebug`
