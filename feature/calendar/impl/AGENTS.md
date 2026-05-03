# :feature:calendar:impl 모듈 가이드

## 역할
- Calendar 기능 구현: 화면 구성과 기능 진입 연결.

## 규칙
- 화면은 상태 중심으로 유지하고 `UiState + Action + SideEffect` 확장 가능 구조를 준비한다.
- 사용자 노출 텍스트는 리소스로 관리한다.
- `ui/screen`은 화면 진입/조립 역할만 유지하고, 세부 뷰는 기능 단위로 `ui/components`에 분리한다.
- ViewModel은 Calendar 화면 상태와 프레젠테이션 로직을 담당한다.
- 날짜 범위, 일정 포함/제외, 완료/기한 지남 해석 같은 서비스 규칙은 use case/domain/entity에 둔다.
- UI mapper는 domain 결과를 날짜 라벨, 섹션, 선택 상태, 빈 상태, SideEffect로 바꾸는 표현 변환만 담당한다.
- Composable/View에는 프레젠테이션 로직, 비즈니스 로직, 애플리케이션 로직을 추가하지 않는다.
- Composable은 ViewModel/UI mapper가 만든 `UiState`/`UiModel`을 그대로 렌더링하고 이벤트만 전달한다.

## 검증
- `./gradlew :feature:calendar:impl:testDebugUnitTest`
- `./gradlew :feature:calendar:impl:lintDebug`
