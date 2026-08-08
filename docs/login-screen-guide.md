# 로그인 화면 작성 가이드 (승엽님용)

## 0. 시작 전에

- 프로젝트 위치: `C:\Users\pey21\projects\job-tracker-android`
- Android Studio로 이 폴더를 열면 됩니다 (File → Open)
- **분업**: 화면 코드는 승엽님이 작성, 빌드/설치/검증은 에이전트가 담당
- 작성이 끝나면 "작성했어"라고 말하면 → 에이전트가 빌드 → 폰에 설치

## 1. 만들 파일 2개

```
app/src/main/java/com/wantfree/jobtracker/presentation/screens/login/
├── LoginViewModel.kt   ← 상태 관리 (로직)
└── LoginScreen.kt      ← 화면 (Compose UI)
```

기존 `ui/screens/home/HomeScreen.kt`는 로그인 성공 후 보여줄 자리이므로 **건드리지 말 것**.

## 2. LoginViewModel.kt

```kotlin
package com.wantfree.jobtracker.presentation.screens.login

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.wantfree.jobtracker.domain.repository.AuthRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

data class LoginUiState(
    val email: String = "",
    val password: String = "",
    val isLoading: Boolean = false,
    val errorMessage: String? = null,
    val isLoggedIn: Boolean = false,   // true가 되면 화면 전환
)

@HiltViewModel
class LoginViewModel @Inject constructor(
    private val authRepository: AuthRepository,
) : ViewModel() {

    private val _uiState = MutableStateFlow(LoginUiState())
    val uiState: StateFlow<LoginUiState> = _uiState.asStateFlow()

    fun onEmailChange(value: String) = _uiState.update { it.copy(email = value) }
    fun onPasswordChange(value: String) = _uiState.update { it.copy(password = value) }

    fun login() {
        val state = _uiState.value
        // 간단한 유효성 검사
        if (state.email.isBlank() || state.password.isBlank()) {
            _uiState.update { it.copy(errorMessage = "이메일과 비밀번호를 입력해주세요") }
            return
        }
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true, errorMessage = null) }
            authRepository.login(state.email, state.password)
                .onSuccess {
                    _uiState.update { it.copy(isLoading = false, isLoggedIn = true) }
                }
                .onFailure { e ->
                    _uiState.update {
                        it.copy(isLoading = false, errorMessage = e.message ?: "로그인 실패")
                    }
                }
        }
    }
}
```

**핵심 개념**:
- `StateFlow` = 상태를 관찰 가능하게 만드는 Flow. UI는 `collectAsState()`로 구독
- `_uiState.update { it.copy(...) }` = 불변 데이터 클래스의 일부만 바꿔 새 상태 생성
- `viewModelScope.launch` = ViewModel 수명주기에서 비동기(네트워크) 실행
- `Result.onSuccess/onFailure` = 성공/실패 분기 처리

## 3. LoginScreen.kt

```kotlin
package com.wantfree.jobtracker.presentation.screens.login

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel

@Composable
fun LoginScreen(
    onLoginSuccess: () -> Unit,
    viewModel: LoginViewModel = hiltViewModel(),
) {
    val state by viewModel.uiState.collectAsState()

    // 로그인 성공 시 화면 전환
    LaunchedEffect(state.isLoggedIn) {
        if (state.isLoggedIn) onLoginSuccess()
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(24.dp),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        Text("Job Tracker", style = MaterialTheme.typography.headlineMedium)

        Spacer(Modifier.height(32.dp))

        OutlinedTextField(
            value = state.email,
            onValueChange = viewModel::onEmailChange,
            label = { Text("이메일") },
            singleLine = true,
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Email),
            modifier = Modifier.fillMaxWidth(),
        )

        Spacer(Modifier.height(12.dp))

        OutlinedTextField(
            value = state.password,
            onValueChange = viewModel::onPasswordChange,
            label = { Text("비밀번호") },
            singleLine = true,
            visualTransformation = PasswordVisualTransformation(),
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password),
            modifier = Modifier.fillMaxWidth(),
        )

        Spacer(Modifier.height(24.dp))

        Button(
            onClick = viewModel::login,
            enabled = !state.isLoading,
            modifier = Modifier.fillMaxWidth(),
        ) {
            if (state.isLoading) {
                CircularProgressIndicator(Modifier.size(20.dp), strokeWidth = 2.dp)
            } else {
                Text("로그인")
            }
        }

        // 에러 메시지
        state.errorMessage?.let { message ->
            Spacer(Modifier.height(16.dp))
            Text(
                message,
                color = MaterialTheme.colorScheme.error,
                style = MaterialTheme.typography.bodyMedium,
            )
        }
    }
}
```

**핵심 개념**:
- `hiltViewModel()` = Hilt가 ViewModel을 자동 주입 (우리가 API 계층 준비해둔 AuthRepository가 들어감!)
- `collectAsState()` = StateFlow 상태를 Compose 상태로 변환 → 값 바뀌면 화면 자동 갱신
- `OutlinedTextField` = Material3 입력 필드 / `PasswordVisualTransformation` = 비밀번호 가림
- `LaunchedEffect` = 상태 변화를 감지해 1회 실행 (여기선 성공 시 화면 전환)

## 4. AppNavHost.kt 수정 (기존 파일)

`core/navigation/AppNavHost.kt`를 열어서 home 화면을 로그인 화면으로 교체:

```kotlin
@Composable
fun AppNavHost() {
    val navController = rememberNavController()
    NavHost(navController = navController, startDestination = "login") {
        composable("login") {
            LoginScreen(
                onLoginSuccess = {
                    navController.navigate("home") {
                        popUpTo("login") { inclusive = true } // 로그인 화면 스택에서 제거
                    }
                }
            )
        }
        composable("home") {
            HomeScreen()
        }
    }
}
```

(import 문: `androidx.navigation.compose.*`, `com.wantfree.jobtracker.presentation.screens.login.LoginScreen`)

## 5. 작성 후

"작성했어"라고 말하면:
1. 에이전트가 빌드 → 에러 있으면 수정 안내
2. 폰에 설치 → 실기기에서 로그인 테스트
3. 테스트 계정: 아직 없으면 회원가입 화면을 다음 단계로 추가

## ⚠️ 주의

- 파일 위치: `presentation/screens/login/` (패키지 `com.wantfree.jobtracker.presentation.screens.login`)
- HomeScreen.kt는 그대로 두기
- Material3 컴포넌트 사용 (material2 아님)
