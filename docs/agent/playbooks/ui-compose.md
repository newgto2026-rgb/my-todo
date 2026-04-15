# Playbook: UI/Compose 변경

## 적용 조건 (When to load)
- Compose 화면/상태/네비게이션 변경
- UI 상태 관리 또는 재구성 최적화 변경

## 수행 순서
1. 화면 상태를 불변 `UiState`로 정의한다.
2. 1회성 이벤트는 `SideEffect`로 분리한다.
3. 사이드 이펙트 로직은 ViewModel/use case에 둔다.
4. 문자열은 `values`, `values-ko` 리소스로 분리한다.
5. 탭/내비게이션은 구현된 feature route와 일치시킨다.
6. 불필요한 재구성을 줄이고 필요 시 안정성 어노테이션을 검토한다.

## 산출물 체크
- UI 동작 테스트(필요 시) 반영
- 상태/이벤트 경계가 코드에서 분명함
