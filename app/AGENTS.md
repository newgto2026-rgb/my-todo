# :app 모듈 가이드

## 역할
- 애플리케이션 셸 및 시작 구성.
- 최상위 내비게이션 호스트와 Android 진입점.
- 런타임 통합(알림, 워커, 리시버).

## 소유 범위
- `MainActivity`, `AppNavHost`, `Application` 연결.
- AndroidManifest 앱 레벨 선언.
- 앱 레벨 리소스와 알림 텍스트.

## 경계
- `feature:*:api`, `feature:*:entry`, `core:*`에 의존한다.
- 기능 내부 구현을 직접 포함하지 않는다.

## 변경 체크리스트
- 내비게이션/시작 목적지가 바뀌면 feature-entry 계약 연결을 검증한다.
- 앱 리소스는 로케일 안전성(`values`, `values-ko`)을 유지한다.
- 리마인더/워커 변경 시 런타임 권한과 채널 동작을 확인한다.

## 검증
- `./gradlew :app:assembleDebug`
- `./gradlew :app:lintDebug`
