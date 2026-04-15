# CI 설정 보고서

## 일자
- 2026-04-12

## 범위
- 코드 푸시 및 PR 생성/업데이트 시 GitHub Actions CI가 자동 실행되도록 설정한다.
- 무엇을 왜 설정했는지 추적 가능한 기록을 남긴다.

## 추가된 항목
- 워크플로 파일 추가:
  - `.github/workflows/android-ci.yml`

## 워크플로 구성

### 워크플로 이름
- `Android CI`

### 트리거
- `push` 대상:
  - `main`
  - `develop`
- `pull_request` 대상:
  - `main`
  - `develop`
- GitHub UI 수동 실행용 `workflow_dispatch`

### 동시성 제어
- 설정:
  - `group: android-ci-${{ github.ref }}`
  - `cancel-in-progress: true`
- 목적:
  - 같은 브랜치/ref에서 이전 실행을 취소하고 최신 실행만 유지한다.

### 권한
- 최소 권한 모델:
  - `contents: read`
- 목적:
  - CI에 최소 권한 원칙을 적용한다.

### 러너
- `ubuntu-latest`
- 타임아웃:
  - `30`분

### CI 단계
1. 소스 체크아웃 (`actions/checkout@v4`)
2. Java 17 설정 (`actions/setup-java@v4`, Temurin)
3. Gradle 캐시 포함 설정 (`gradle/actions/setup-gradle@v4`)
4. `gradlew` 실행 권한 보장
5. 단위 테스트 실행:
   - `./gradlew --stacktrace testDebugUnitTest`
6. 디버그 빌드 아티팩트 생성:
   - `./gradlew --stacktrace assembleDebug`
7. 정적 분석 실행:
   - `./gradlew --stacktrace lintDebug`
8. 아티팩트 업로드(항상):
   - 단위 테스트 결과/리포트
   - 린트 리포트
   - 일반 빌드 리포트/로그

## 업로드 아티팩트
- `test-reports`
  - `**/build/test-results/**`
  - `**/build/reports/tests/**`
- `lint-reports`
  - `**/build/reports/lint-results*.html`
  - `**/build/reports/lint-results*.xml`
- `build-reports`
  - `app/build/reports/**`
  - `**/build/outputs/logs/**`

## 기대 동작
- `main` 또는 `develop`으로의 모든 push에서 CI가 자동 시작된다.
- `main` 또는 `develop` 대상 모든 PR에서 CI가 자동 시작된다.
- 실패 시 stacktrace와 다운로드 가능한 리포트로 원인 진단이 가능하다.

## 권장 GitHub 저장소 설정
- `main` 브랜치 보호 활성화:
  - 머지 전 PR 필수
  - 머지 전 상태 체크 통과 필수
  - 체크 항목: `Build, Unit Test, Lint`
- 선택 사항:
  - `develop`에도 동일 규칙 적용
  - 선형 히스토리 강제
  - 새 커밋 푸시 시 기존 승인 무효화

## 참고
- 이 설정은 에뮬레이터 기반 계측 테스트(`connectedDebugAndroidTest`)를 실행하지 않는다. 해당 작업은 일반적으로 더 느리므로 전용 워크플로로 분리하는 것을 권장한다.
- 기존 로컬 미커밋 IDE 파일은 의도적으로 변경하지 않았다.
