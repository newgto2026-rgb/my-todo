# 프로젝트 에이전트 가이드 (인덱스)

## 목적
- 이 문서는 에이전트 작업의 단일 진입점이다.
- 코드 변경 시 필요한 필수 규칙과 체크리스트를 이 문서에서 바로 확인한다.

## 코드 변경 시작 전 필수 체크
1. 현재 작업 경로가 기본 체크아웃이 아닌지 확인한다.
2. 전용 브랜치 + 별도 Git worktree에서 작업한다.
3. 변경 대상 Gradle 모듈을 식별하고 대상 모듈 `AGENTS.md`를 연다.
4. 모듈 경계/의존 방향 위반 가능성을 먼저 점검한다.

## 코드 변경 완료 전 필수 체크
1. 영향 모듈 단위 테스트를 실행한다.
2. 영향 모듈 린트를 실행한다.
3. 필요 시 앱 빌드 또는 통합 테스트를 실행한다.
4. 테스트 결과와 변경 모듈을 PR 설명에 명시한다.

## 전역 필수 정책
### 작업/브랜치 정책
- 구현 작업은 전용 브랜치 + 별도 Git worktree에서 수행한다.
- 기본 체크아웃에서 직접 구현하지 않는다.
- `main`에 직접 푸시하지 않는다.
- 푸시 시 반드시 PR을 생성한다.

### 변경 범위/품질 정책
- 변경은 최소 범위, 테스트 가능, 기능 중심으로 유지한다.
- 모든 구현 변경은 같은 PR에 자동화 테스트(단위/통합)를 포함한다.
- non-view 레이어(Compose/View UI 제외) 커버리지 80% 이상을 목표로 한다.
- PR 커밋 메시지는 구조적이고 설명 가능하게 작성한다.
- use case/ViewModel 단위 테스트를 우선한다.
- 핵심 사용자 플로우에는 UI 테스트를 추가한다.
- Flow 테스트에는 Turbine, 공용 fake/rule은 `core:testing`을 재사용한다.

### 아키텍처/의존 정책
- 모듈 경계와 의존 방향을 지킨다.
- `core:* -> feature:*` 의존을 추가하지 않는다.
- API 계약(`feature:*:api`)과 구현(`feature:*:impl`) 변경 범위를 분리한다.
- UI는 UDF(`UiState + ViewModel + Action/SideEffect`)를 우선한다.
- 사이드 이펙트는 컴포저블 내부가 아닌 ViewModel/use case 레이어에서 처리한다.

### UI/리소스 정책
- 사용자 노출 문자열은 `values`, `values-ko` 리소스로 관리한다.
- 최상위 탭은 구현된 feature 라우트에만 매핑한다.
- 탭 아이콘은 텍스트 글리프 대체가 아닌 명시적 아이콘 에셋을 사용한다.

### PR 설명 정책
- 모든 PR 설명은 `.github/pull_request_template.md`를 따른다.

## 기본 검증 명령어
- 앱 빌드: `./gradlew assembleDebug`
- 단위 테스트: `./gradlew testDebugUnitTest`
- 계측 테스트: `./gradlew connectedDebugAndroidTest`
- 모듈 단위 테스트: `./gradlew :<module>:testDebugUnitTest`
- 모듈 린트: `./gradlew :<module>:lintDebug`

## 빠른 사용 순서
1. 이 루트 가이드를 읽는다.
2. 대상 Gradle 모듈을 식별한다.
3. 대상 모듈의 `AGENTS.md`만 연다.
4. 루트의 `코드 변경 시작 전 필수 체크`를 통과한 뒤 작업을 시작한다.
5. 루트의 `코드 변경 완료 전 필수 체크`를 통과한 뒤 작업을 종료한다.

## 분리 문서
- 인덱싱/로드 규칙: `docs/agent/indexing.md`
- 테스트/커버리지/검증 커맨드: `docs/agent/quality-gates.md`
- 구현 플레이북: `docs/agent/playbooks/feature-impl.md`
- UI/Compose 플레이북: `docs/agent/playbooks/ui-compose.md`
- Data/Domain/Network 플레이북: `docs/agent/playbooks/data-layer.md`
- 위 문서들은 상세 참고용이다. 실행 필수 항목은 이 루트 문서를 기준으로 판단한다.

## 기술 기준선
- Kotlin + Coroutines
- Jetpack Compose + Material 3
- Hilt DI
- Navigation Compose
- Room + DataStore
- Retrofit + OkHttp
- WorkManager

## 모듈 인덱스
| Gradle 모듈 | 가이드 경로 | 책임 |
|---|---|---|
| `:app` | `app/AGENTS.md` | 앱 셸, 시작 구성, 최상위 연결 |
| `:core:model` | `core/model/AGENTS.md` | 순수 도메인/데이터 모델 |
| `:core:domain` | `core/domain/AGENTS.md` | 유스케이스 + 리포지토리 계약 |
| `:core:data` | `core/data/AGENTS.md` | 리포지토리 구현 + 매퍼 |
| `:core:database` | `core/database/AGENTS.md` | Room DB/Entity/DAO/마이그레이션 |
| `:core:datastore` | `core/datastore/AGENTS.md` | 환경설정 데이터 소스 레이어 |
| `:core:network` | `core/network/AGENTS.md` | 원격 API/네트워킹 계약 |
| `:core:ui` | `core/ui/AGENTS.md` | 재사용 UI 프리미티브 |
| `:core:designsystem` | `core/designsystem/AGENTS.md` | 테마/타이포/디자인 토큰 |
| `:core:testing` | `core/testing/AGENTS.md` | 공용 테스트 fake/rule/helper |
| `:feature:todo:api` | `feature/todo/api/AGENTS.md` | Todo 공개 계약/라우트 |
| `:feature:todo:impl` | `feature/todo/impl/AGENTS.md` | Todo UI + ViewModel + 기능 로직 |
| `:feature:todo:entry` | `feature/todo/entry/AGENTS.md` | 앱 연결용 todo 진입 바인딩 |
| `:feature:calendar:api` | `feature/calendar/api/AGENTS.md` | Calendar 공개 계약/라우트 |
| `:feature:calendar:impl` | `feature/calendar/impl/AGENTS.md` | Calendar UI + 기능 로직 |
| `:feature:calendar:entry` | `feature/calendar/entry/AGENTS.md` | 앱 연결용 calendar 진입 바인딩 |

## 현재 의존 구조
- `app -> feature:*:api, feature:*:entry, core:*`
- `feature:*:entry -> feature:*:api, feature:*:impl`
- `feature:*:impl -> feature:*:api, core:*`
- `core:data -> core:domain + storage modules`
- `core:*`는 `feature:*`에 의존하지 않음
