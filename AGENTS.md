# 프로젝트 에이전트 가이드 (인덱스)

## 목적
- 이 문서는 진입 인덱스 역할만 수행한다.
- 세부 규칙은 작업 유형에 맞는 별도 문서만 선택 로드한다.

## 빠른 사용 순서
1. 이 루트 가이드를 읽는다.
2. 대상 Gradle 모듈을 식별한다.
3. 대상 모듈의 `AGENTS.md`만 연다.
4. 필요할 때만 아래 분리 문서를 추가 로드한다.

## 분리 문서
- 인덱싱/로드 규칙: `docs/agent/indexing.md`
- 전역 정책(브랜치/PR/의존/UI 기본 규칙): `docs/agent/policies/global-rules.md`
- 테스트/커버리지/검증 커맨드: `docs/agent/quality-gates.md`
- 구현 플레이북: `docs/agent/playbooks/feature-impl.md`
- UI/Compose 플레이북: `docs/agent/playbooks/ui-compose.md`
- Data/Domain/Network 플레이북: `docs/agent/playbooks/data-layer.md`

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
