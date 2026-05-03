# :feature:todo:entry 모듈 가이드

## 역할
- 앱 조합을 위해 todo 기능 진입 구현을 바인딩하는 연결 모듈.

## 규칙
- DI/와이어링 관심사만 둔다.
- 비즈니스 로직이나 UI 구현을 넣지 않는다.
- use case 선택, UiState 변환, Composable 렌더링 정책을 entry 모듈에 두지 않는다.

## 변경 체크리스트
- API/impl 클래스명이 바뀌면 Hilt 바인딩을 갱신한다.
- 앱에서 feature entry set이 정상 해석되는지 확인한다.

## 검증
- `./gradlew :feature:todo:entry:lintDebug`
