# Job Tracker Android

채용 공고 추적 앱 — job-tracker(2번 프로젝트) 백엔드 API의 Android 클라이언트.

## 기술 스택 (버전 변경 금지!)
- Kotlin 2.0.21, AGP 8.7.3, Gradle 8.9 (wrapper)
- Compose BOM 2024.12.01, Material3, Navigation Compose 2.8.5
- Hilt 2.53.1 (+ KSP 2.0.21-1.0.27), Retrofit 2.11.0, OkHttp 4.12.0
- kotlinx-serialization-json 1.7.3, Room 2.6.1, DataStore 1.1.1
- compileSdk 36, minSdk 26, targetSdk 36

## 패키지 구조 (클린 아키텍처 4레이어)
```
com.wantfree.jobtracker/
├── core/di/          → DI 모듈 (NetworkModule, DataModule, RepositoryModule, AppModule)
├── core/navigation/  → AppNavHost (화면 이동 규칙 — core에 있음!)
├── data/             → api/AuthService, model/auth/DTO, local/TokenManager, repository/AuthRepositoryImpl
├── domain/           → repository/AuthRepository (인터페이스 — UI가 바라보는 계약)
└── presentation/     → screens/login(LoginScreen, LoginViewModel), screens/home, theme
```

## 백엔드 API (Render 배포)
- BASE_URL: `https://job-tracker-so4v.onrender.com/`
- POST /api/auth/login → { email, password } → { accessToken, tokenType, expiresIn }
- POST /api/auth/signup → { email, password(min 8), nickname(2~20자) } → { id, email, nickname, createdAt, keywords }
- Render 무료 플랜이라 15분 후 콜드 스타트 (첫 요청 최대 50초 대기)

## 빌드 / 설치
```bash
export JAVA_HOME="/c/Program Files/Android/Android Studio/jbr"   # JDK 21 (AS 번들)
export ANDROID_HOME="$LOCALAPPDATA/Android/Sdk"
./gradlew assembleDebug
adb install -r app/build/outputs/apk/debug/app-debug.apk
```

## ⚠️ 함정 (알고 있어야 하는 것)
1. **compose foundation 1.7.6에 `VerticalScrollbar` / `rememberScrollbarAdapter` API가 없다!**
   → LoginScreen에 커스텀 스크롤바를 `Modifier.drawBehind`로 직접 구현해둠 (scrollState.maxValue/value로 thumb 계산, 스크롤 가능할 때만 표시)
2. **material3 Text에 `brush` 파라미터 없음** → 그라디언트 텍스트는 `TextStyle.copy(brush = ...)` 사용
3. **themes.xml은 `android:Theme.Material.NoActionBar`** 사용 (Material3 뷰 테마는 라이브러리에 없음 — Compose라 불필요)
4. **compileSdk 36은 AGP 8.7.3 기준 비공식** → gradle.properties에 `android.suppressUnsupportedCompileSdk=36`
5. **MainActivity는 immersive 모드** (상태바/네비게이션바 숨김) — 화면 전체가 앱 배경
6. Retrofit 변환은 `retrofit2-kotlinx-serialization-converter`(jakewharton 1.0.0) — `json.asConverterFactory()` 사용

## 작업 이력 (최신순)
- 2026-08-09: 스크롤바 커스텀 구현(drawBehind) + 스크롤 가능할 때만 표시로 확정
- 2026-08-09: 로그인/회원가입 화면 — 웹(job-tracker frontend) 디자인 재현 (그라디언트 배경, 흰색 카드, 토스트 오버레이, 비밀번호 눈 토글, 가입 후 자동 로그인)
- 2026-08-09: immersive 전체 화면 모드 적용
- 2026-08-09: API 계층 구축 (AuthService, DTO, AuthRepository + Impl, TokenManager/DataStore, DI)
- 2026-08-09: 스캐폴딩 (Compose + Hilt + Retrofit + Room, version catalog)

## 진행 중 / 다음
- 로그인/회원가입 화면 완성 (실기기 테스트 대기)
- 다음: 홈 화면(공고 목록) — 웹 스타일 재현 예정
- UI 분업: 화면은 Hermes가 작성, 승엽님이 UX 확인/피드백
