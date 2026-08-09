# Job Tracker Android

채용 공고 지원 현황을 한눈에 관리하는 안드로이드 앱. 취업 준비 과정에서 실제로 필요한 기능을 직접 설계하고 만들었습니다.

백엔드는 [job-tracker](https://github.com/wantfree8937/job-tracker) (Spring Boot, Render 배포) API를 사용합니다.

## 주요 기능

- 로그인 / 회원가입 (JWT 인증, DataStore 토큰 저장)
- 내 공고 목록 — 지원 상태별 통계 및 필터 (지원 예정 / 지원함 / 면접 / 합격 / 불합격)
- 공고 등록 / 수정 / 삭제 / 상세 (마감일, 링크, 메모)
- 키워드 검색

## 기술 스택

- Kotlin 2.0.21, Jetpack Compose (Material3)
- 클린 아키텍처 4레이어: core / data / domain / presentation
- Hilt (의존성 주입), Retrofit + OkHttp + kotlinx-serialization
- Room, DataStore, Navigation Compose

## 프로젝트 구조

```
com.wantfree.jobtracker/
├── core/          # DI 모듈, 네비게이션
├── data/          # API, DTO, 로컬 저장소, Repository 구현
├── domain/        # Repository 인터페이스 (UI가 바라보는 계약)
└── presentation/  # Compose 화면 (로그인/홈/등록/상세), ViewModel, 테마
```

## 실행 방법

1. Android Studio로 프로젝트를 연다.
2. USB 디버깅을 켠 실기기(또는 에뮬레이터)를 연결한다.
3. Run (또는 `./gradlew assembleDebug` 후 `adb install -r app/build/outputs/apk/debug/app-debug.apk`).

## 백엔드

- Base URL: `https://job-tracker-so4v.onrender.com`
- API 명세: [job-tracker](https://github.com/wantfree8937/job-tracker) 저장소 참고
