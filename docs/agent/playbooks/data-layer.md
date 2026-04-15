# Playbook: Data/Domain/Network 변경

## 적용 조건 (When to load)
- 리포지토리, DTO, 매퍼, DB, DataStore, 네트워크 계약 변경

## 수행 순서
1. DTO/전송 모델과 도메인 모델을 분리한다.
2. mapper/contract 경계를 명시적으로 유지한다.
3. 실패 경로를 `Result` 또는 표준 에러 모델로 통일한다.
4. 페이지네이션은 기본적으로 Paging 3를 사용한다.
5. DB 스키마/키 변경은 마이그레이션 안전하게 설계한다.
6. 리포지토리 계약은 관심사별로 좁게 유지한다.

## 산출물 체크
- use case/ViewModel 소비 관점 테스트 포함
- 마이그레이션/직렬화/매퍼 회귀 확인
