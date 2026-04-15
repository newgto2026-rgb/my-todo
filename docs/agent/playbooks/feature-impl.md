# Playbook: Feature 구현

## 적용 조건 (When to load)
- 새 기능 추가/확장
- ViewModel + UseCase + Repository 변경이 함께 발생하는 구현

## 수행 순서
1. 대상 모듈과 PRD ID를 확정한다.
2. 모듈 경계/의존 방향을 확인한다.
3. API 계약(`feature:*:api`)과 구현(`feature:*:impl`) 변경 범위를 분리한다.
4. UI는 `UiState + Action + SideEffect` UDF로 설계한다.
5. 문자열/아이콘/리소스 정책을 반영한다.
6. 모듈 단위 테스트를 먼저 작성/보강한다.
7. 영향 모듈 린트 및 테스트를 실행한다.

## 산출물 체크
- 변경 모듈 명시
- 테스트 결과 명시
- PR 설명 템플릿 준수
