# :feature:todo:impl 모듈 가이드

## 역할
- Todo 기능 구현: 컴포저블, ViewModel, 상태 전이.

## 규칙
- 하나의 화면 플로우는 `UiState + Action + SideEffect + ViewModel` 구조를 유지한다.
- 사이드 이펙트는 ViewModel/use case 레이어에서 처리한다.
- 사용자 노출 텍스트는 리소스에서 가져온다.

## UI 폴더 규약
- `ui/screen/`: 라우트 + 화면 진입 구성(`TodoListRoute`, 화면 레벨 연결).
- `ui/components/`: 재사용 가능한 화면 하위 뷰(헤더/필터/빈 상태).
- `ui/editor/`: Todo 편집 바텀시트 섹션 및 날짜/시간 편집 헬퍼.
- `ui/sheet/`: 편집 외 모달 시트(카테고리 관리자 등).
- 상태 로직 파일(`UiState`, `Action`, `SideEffect`, `ViewModel`, mapper/validator)은 `ui/` 루트에 둔다.

## 변경 체크리스트
- 상태 매핑 체인을 검증한다:
  1) repository/use case 출력
  2) UI 매퍼
  3) ViewModel 상태 + 사이드 이펙트
  4) 컴포저블 렌더링
- 내비게이션 동작 추가 시 계약은 `:feature:todo:api`에 둔다.

## 검증
- `./gradlew :feature:todo:impl:testDebugUnitTest`
- `./gradlew :feature:todo:impl:lintDebug`
