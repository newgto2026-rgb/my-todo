# 구현 계획 - Priority v0.2+

- 기준 문서: `docs/PRD-Priority-v0.2.md`
- 작성일: 2026-04-15
- 상태: Ready for Implementation
- 작업 원칙: 별도 worktree + 전용 브랜치에서만 작업

## 1) 목표와 완료 기준
### 1.1 구현 목표
- 카테고리 기능을 사용자 플로우에서 제거한다.
- 중요도(`LOW`, `MEDIUM`, `HIGH`)를 데이터/도메인/UI 전 계층에 도입한다.
- 목록/오늘/캘린더에서 중요도 기준 가시성과 정렬 일관성을 확보한다.

### 1.2 최종 완료 기준 (Release Gate)
- 기능: 카테고리 UI/로직 제거 + 중요도 입력/표시/정렬/필터/Undo 동작
- 데이터: DB 마이그레이션 성공, 기존 데이터 손실 0
- 품질: 모듈 테스트/린트 통과 + non-view 레이어 커버리지 기준 충족
- 검증: 에뮬레이터 수동 QA 체크리스트 100% 통과

## 2) 모듈별 변경 범위 (확정)
### 2.1 `:core:model`
- `TodoPriority` enum 추가: `LOW`, `MEDIUM`, `HIGH`
- Todo 모델에 `priority` 필드 추가

### 2.2 `:core:database`
- Todo Entity `priority` 컬럼 추가 (TEXT, NOT NULL, default `MEDIUM`)
- DB version 증가 + Migration 구현
- 카테고리 스키마는 1차 릴리스에서 유지(deprecate), 2차 릴리스에서 삭제

### 2.3 `:core:data`
- DTO/Entity/Domain 매퍼에 priority 매핑 추가
- category 관련 저장/조회 경로는 단계적 제거

### 2.4 `:core:domain`
- 정렬 규칙 반영: 미완료 -> 중요도 -> due date/time
- 필터 계약 반영: 전체/높음/중간/낮음

### 2.5 `:core:datastore`
- 중요도 필터 상태 저장 키 추가
- 재실행 시 필터 상태 복원

### 2.6 `:core:ui`
- `TodoItemRow` API를 priority 기반으로 확장
- 카테고리 배지 의존 API 제거 또는 deprecated 처리

### 2.7 `:feature:todo:impl`
- 카테고리 필터/관리 UI 제거
- Add/Edit 화면에 중요도 선택 컴포넌트 추가
- 리스트 중요도 배지/정렬/필터 반영
- 완료/미완료/강조 상태 톤 정리
- 중요도 변경/완료 토글 Undo 연결

### 2.8 `:feature:calendar:impl`
- 날짜 상세 리스트에 중요도 표시
- 날짜 상세 정렬 priority 우선 반영

### 2.9 리소스
- `values`, `values-ko`에 중요도 라벨/설명/온보딩 문구 추가
- 카테고리 관련 사용자 문구 제거

## 3) 단계별 실행 계획 (의존성 포함)
## Phase 0 - 영향도 분석 (0.5일)
1. category 참조 코드 전수 검색 및 삭제 후보 표 작성
2. DB 마이그레이션 전략 확정
3. 작업 쪼개기(커밋 단위) 확정

산출물:
- 영향도 표
- 마이그레이션 설계 메모

## Phase 1 - 데이터/도메인 기반 구축 (1일)
1. model/database/data/domain에 priority 경로 추가
2. migration 테스트, mapper/usecase 테스트 작성
3. 기존 기능 회귀 테스트

산출물:
- priority end-to-end data path
- migration + unit tests

## Phase 2 - Todo UX 전환 (1.5일)
1. Add/Edit 중요도 입력 추가
2. 리스트 정렬/필터/배지 반영
3. 카테고리 UI 제거
4. Undo 연결

산출물:
- todo 주요 플로우 전환 완료
- 스크린샷(ALL/TODAY/COMPLETED)

## Phase 3 - Calendar 전환 (0.5일)
1. 날짜 상세 중요도 표시
2. 날짜 상세 priority 정렬

산출물:
- calendar 상세 반영 + 회귀 테스트

## Phase 4 - 정리/릴리스 준비 (0.5일)
1. 미사용 category 코드 청소
2. 온보딩/문구/접근성 검수
3. 최종 검증(테스트/린트/설치/수동 QA)

산출물:
- PR 최종본 + 릴리스 노트

## 4) 테스트 전략 (강화)
### 4.1 단위 테스트
- priority 정렬/필터 유스케이스
- ViewModel 상태 전이(필터 유지, Undo)
- mapper/serializer round-trip

### 4.2 마이그레이션 테스트
- old schema -> new schema 업그레이드 검증
- priority default=`MEDIUM` 적용 검증
- row count 및 핵심 필드 보존 검증

### 4.3 UI 테스트
- 테스트 범위: `app/src/androidTest` (통합 UI 시나리오)
- 우선순위: P0(릴리스 차단), P1(회귀 방지), P2(보강)

#### P0 - 핵심 플로우 (반드시 자동화)
1. `UI-P0-01` 새 할 일 기본 중요도 검증
- 사전조건: 앱 첫 진입, 빈 제목 입력 가능 상태
- 절차: Add FAB -> Edit 화면 진입 -> 제목 입력 -> 저장
- 기대결과: 생성된 항목 중요도는 `중간`, 리스트에 즉시 반영

2. `UI-P0-02` Edit 화면에서 중요도 변경 저장
- 사전조건: 기존 할 일 1개 존재
- 절차: 해당 아이템 클릭 -> Edit 화면 -> 중요도 `높음` 선택 -> 저장
- 기대결과: 리스트 아이템 중요도 배지가 `높음`으로 변경, 재진입 후 유지

3. `UI-P0-03` 중요도 정렬 규칙 검증
- 사전조건: 미완료 3개(낮음/중간/높음), 완료 1개 존재
- 절차: ALL 탭 진입
- 기대결과: 미완료가 상단, 내부 순서는 `높음 > 중간 > 낮음`, 완료는 하단

4. `UI-P0-04` 중요도 필터 동작 검증
- 사전조건: 낮음/중간/높음 데이터 준비
- 절차: 필터를 `높음` 선택
- 기대결과: `높음`만 노출, 다른 중요도 미노출

5. `UI-P0-05` 완료 토글 + Undo
- 사전조건: 미완료 항목 존재
- 절차: 체크 토글 -> 스낵바 Undo 클릭
- 기대결과: 원래 미완료 상태로 복구, 순서도 원상복귀

6. `UI-P0-06` 캘린더 상세 중요도 표시/정렬
- 사전조건: 같은 날짜에 서로 다른 중요도 항목 3개 존재
- 절차: Calendar 탭 -> 해당 날짜 선택
- 기대결과: 상세 리스트에서 중요도 노출, 정렬은 `높음 > 중간 > 낮음`

#### P1 - 상태 유지/회귀
1. `UI-P1-01` 필터 상태 재진입 유지
- 절차: `높음` 필터 적용 -> 탭 이동/앱 재생성 후 복귀
- 기대결과: 필터 상태 유지

2. `UI-P1-02` Edit 취소 동작
- 절차: Edit 화면에서 중요도 변경 -> 저장 없이 닫기
- 기대결과: 기존 중요도 유지

3. `UI-P1-03` 완료 상태에서 중요도 변경
- 절차: 완료 항목 Edit -> 중요도 변경 저장
- 기대결과: 완료 상태 유지, 중요도만 변경

#### P2 - UX/접근성
1. `UI-P2-01` 색상 외 텍스트로 중요도 인지 가능
- 기대결과: 배지 텍스트(`낮음/중간/높음`)만으로도 구분 가능

2. `UI-P2-02` 긴 제목 + 중요도 배지 레이아웃 안정성
- 기대결과: 줄바꿈/잘림이 기능을 방해하지 않음

### 4.4 Edit 화면 전용 테스트 설계 (요청 반영)
- 대상 화면: `TodoEditBottomSheet` (신규 중요도 선택 UI 포함)
- 필수 testTag(추가/유지)
  - `task_title_input` (기존)
  - `save_button` (기존)
  - `priority_low_option` (신규)
  - `priority_medium_option` (신규)
  - `priority_high_option` (신규)
  - `priority_section` (신규)
- Edit 화면 케이스
  - `EDIT-01` 진입 시 현재 중요도 pre-select 확인
  - `EDIT-02` 중요도 변경 후 저장 시 반영 확인
  - `EDIT-03` 중요도만 변경(제목/날짜 미변경) 저장 가능
  - `EDIT-04` 잘못된 입력(빈 제목)에서 중요도 선택 상태 유지
  - `EDIT-05` 삭제/닫기 분기에서 중요도 변경이 누수되지 않음

### 4.5 테스트 데이터 픽스처 전략
- androidTest 전용 seed helper 추가(낮음/중간/높음 + 완료/미완료 + 날짜 분산)
- 테스트 간 독립성 보장: 각 테스트 시작 시 DB 초기화 및 fixture 재삽입
- 시간/날짜 의존성 고정: `LocalDate.now()` 기준 케이스는 상대 날짜 helper 사용

### 4.6 검증 명령 (Gate)
- `./gradlew :feature:todo:impl:testDebugUnitTest`
- `./gradlew :feature:calendar:impl:testDebugUnitTest`
- `./gradlew :core:database:testDebugUnitTest`
- `./gradlew :feature:todo:impl:lintDebug :feature:calendar:impl:lintDebug`
- `./gradlew :feature:todo:impl:jacocoCoverageVerification :feature:calendar:impl:jacocoCoverageVerification`
- `./gradlew :app:connectedDebugAndroidTest`
- `./gradlew :app:installDebug`

## 5) 데이터 마이그레이션/롤백 계획
### 5.1 마이그레이션
- add column: `priority TEXT NOT NULL DEFAULT 'MEDIUM'`
- 기존 todo는 일괄 `MEDIUM`으로 이관

### 5.2 롤백
- 앱 코드 롤백 시 스키마 하향 불가 가능성 고려
- 안전 롤백 기준: forward-compatible reader 유지 (unknown/nullable 대비)
- 치명 이슈 시 hotfix: priority 값을 무시하고 MEDIUM으로 해석

## 6) 리스크 매트릭스
1. 리스크: shared `TodoItemRow` 변경으로 화면 전반 톤 불일치
- 대응: ALL/TODAY/COMPLETED 캡처 비교 + 디자인 토큰 고정
2. 리스크: migration 오류
- 대응: migration instrumentation/unit test 필수 + 샘플 DB 검증
3. 리스크: 카테고리 제거로 일부 사용자 혼란
- 대응: 첫 진입 안내 문구 + 릴리스 노트 명확화
4. 리스크: 중요도 남용(전부 높음)
- 대응: 후속 백로그에 usage insight/가이드 UX 반영

## 7) 관측/지표 수집 계획
- 이벤트(로컬 로그 또는 분석 SDK 연결 가능 시):
  - `todo_priority_set`
  - `todo_priority_filter_changed`
  - `todo_undo_clicked`
  - `todo_completed_toggled`
- 릴리스 후 1주 관찰 지표:
  - 높은 중요도 완료율
  - 필터 사용률
  - Undo 발생률

## 8) 커밋/PR 계획
1. `feat(priority): add priority model and database migration`
2. `feat(todo): replace category flows with priority controls`
3. `feat(calendar): expose and sort todos by priority`
4. `chore(copy): remove category strings and add priority wording`
5. `test(priority): add migration and sorting/filtering coverage`

PR 전략:
- 권장: 2개 PR
  - PR-A: 데이터/도메인/마이그레이션
  - PR-B: UI/UX 전환(todo+calendar)
- 각 PR은 템플릿 준수, 스크린샷 포함

## 9) 오픈 이슈 (결정 필요)
1. 카테고리 스키마 제거 시점(이번/다음 릴리스)
2. 중요도 색상 토큰 최종값
3. 완료 상태에서 중요도 배지 표시 강도
4. 온보딩 노출 정책(1회 고정 여부)

## 10) DoD 체크리스트
- [ ] category UI/로직 제거 완료
- [ ] priority 3단계 입력/표시/정렬/필터 동작
- [ ] migration 테스트 통과
- [ ] lint/test/coverage gate 통과
- [ ] 에뮬레이터 수동 QA 통과
- [ ] PR 템플릿 + before/after 스크린샷 첨부
