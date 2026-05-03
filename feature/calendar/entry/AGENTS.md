# :feature:calendar:entry 모듈 가이드

## 역할
- Calendar 기능의 Hilt 멀티바인딩 진입점 등록.

## 규칙
- `AppFeatureEntry` set 멀티바인딩으로만 바인딩한다.
- `app` 모듈이 `:feature:calendar:impl`에 직접 결합되지 않게 유지한다.
- 비즈니스 로직, 프레젠테이션 로직, UI 구현을 entry 모듈에 넣지 않는다.

## 검증
- `./gradlew :feature:calendar:entry:assembleDebug`
