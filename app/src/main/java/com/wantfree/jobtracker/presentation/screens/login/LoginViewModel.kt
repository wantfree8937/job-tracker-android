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

/** 로그인/회원가입 화면 상태 */
data class LoginUiState(
    val isSignup: Boolean = false,          // false=로그인, true=회원가입
    val email: String = "",
    val password: String = "",
    val passwordConfirm: String = "",
    val nickname: String = "",
    val isLoading: Boolean = false,
    val errorMessage: String? = null,
    val successMessage: String? = null,
    val isLoggedIn: Boolean = false,        // true가 되면 화면 전환
)

@HiltViewModel
class LoginViewModel @Inject constructor(
    private val authRepository: AuthRepository,
) : ViewModel() {

    companion object {
        // 웹 프론트와 동일한 이메일 정규식
        private val EMAIL_REGEX = Regex("^[^\\s@]+@[^\\s@]+\\.[^\\s@]+$")
    }

    private val _uiState = MutableStateFlow(LoginUiState())
    val uiState: StateFlow<LoginUiState> = _uiState.asStateFlow()

    fun onEmailChange(value: String) = _uiState.update { it.copy(email = value) }
    fun onPasswordChange(value: String) = _uiState.update { it.copy(password = value) }
    fun onPasswordConfirmChange(value: String) = _uiState.update { it.copy(passwordConfirm = value) }
    fun onNicknameChange(value: String) = _uiState.update { it.copy(nickname = value) }

    /** 로그인 ↔ 회원가입 전환 */
    fun toggleMode() = _uiState.update {
        it.copy(isSignup = !it.isSignup, errorMessage = null, successMessage = null)
    }

    fun clearError() = _uiState.update { it.copy(errorMessage = null) }
    fun clearSuccess() = _uiState.update { it.copy(successMessage = null) }

    /** 로그인 또는 회원가입 제출 (모드에 따라 분기 — 웹과 동일 동작) */
    fun submit() {
        val state = _uiState.value

        // 공통: 이메일 형식 검사
        if (!EMAIL_REGEX.matches(state.email)) {
            _uiState.update { it.copy(errorMessage = "올바른 이메일 형식이 아닙니다") }
            return
        }

        if (state.isSignup) {
            // 회원가입 유효성 검사
            if (state.password != state.passwordConfirm) {
                _uiState.update { it.copy(errorMessage = "비밀번호가 일치하지 않습니다") }
                return
            }
            if (state.nickname.isBlank()) {
                _uiState.update { it.copy(errorMessage = "닉네임을 입력해주세요") }
                return
            }
            signUpAndLogin(state)
        } else {
            login(state)
        }
    }

    private fun login(state: LoginUiState) {
        if (state.password.isBlank()) {
            _uiState.update { it.copy(errorMessage = "비밀번호를 입력해주세요") }
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

    /** 가입 직후 바로 로그인 — 웹과 동일 (재입력 없이 메인 진입) */
    private fun signUpAndLogin(state: LoginUiState) {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true, errorMessage = null) }
            authRepository.signUp(state.email, state.password, state.nickname)
                .onSuccess {
                    authRepository.login(state.email, state.password)
                        .onSuccess { token ->
                            _uiState.update {
                                it.copy(
                                    isLoading = false,
                                    successMessage = "회원가입이 완료되었습니다.",
                                    isLoggedIn = true,
                                )
                            }
                        }
                        .onFailure { e ->
                            _uiState.update {
                                it.copy(isLoading = false, errorMessage = e.message ?: "로그인 실패")
                            }
                        }
                }
                .onFailure { e ->
                    _uiState.update {
                        it.copy(isLoading = false, errorMessage = e.message ?: "회원가입 실패")
                    }
                }
        }
    }
}
