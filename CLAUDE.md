# Ember API

Kotlin + Spring Boot 4 API 서버.

## 빌드 & 실행

```bash
./gradlew build
./gradlew bootRun
```

## 테스트

```bash
./gradlew test
```

## 코드 품질

```bash
./gradlew spotlessApply   # 코드 자동 포맷
./gradlew spotlessCheck   # 포맷 검사
./gradlew detekt          # 정적 분석
```

## 필수 규칙

- 작업 완료 후 커밋 전 반드시 아래 순서로 검증:
  1. `./gradlew spotlessApply` (코드 포맷)
  2. `./gradlew detekt` (정적 분석)
  3. `./gradlew test` (테스트 실행)
  4. 실패 시 원인 분석 → 해결 후 재실행 → 통과 확인
- 검증 통과 전에는 절대 커밋하지 않는다
- 모킹은 Mockito 대신 MockK 사용
- 테스트는 Kotlin + JUnit5로 작성

## 커밋 규칙

- `feat: 기능 추가`, `fix: 버그 수정` 형태
- 서술형(`했습니다`) 대신 명사형(`추가`, `수정`, `개선`)
- 제목 아래 본문에 변경 내용을 구체적으로 작성:
  ```
  feat: 코드 품질 도구 추가

  - Spotless + ktlint 포맷팅 설정 추가
  - detekt 정적 분석 설정 및 detekt.yml 추가
  - MockK 테스트 의존성 추가
  ```
