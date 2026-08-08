package com.wantfree.jobtracker.presentation.screens.login

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxScope
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.filled.VisibilityOff
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import kotlinx.coroutines.delay

// 웹 프론트(index.css) 디자인 토큰과 동일
private val Indigo = Color(0xFF6366F1)
private val Purple = Color(0xFF8B5CF6)
private val Pink = Color(0xFFD946EF)
private val TextDark = Color(0xFF0F172A)
private val TextGray = Color(0xFF64748B)
private val BorderGray = Color(0xFFE5E7EB)
private val SurfaceWhite = Color(0xFFFFFFFF)
private val ErrorRed = Color(0xFFDC2626)
private val SuccessGreen = Color(0xFF15803D)

private val BrandGradient = Brush.linearGradient(listOf(Indigo, Purple))
private val PageBackground = Brush.linearGradient(listOf(Indigo, Purple, Pink))

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

    // 에러/성공 토스트 3초 후 자동 제거 (웹과 동일 — 레이아웃 밀림 없음)
    LaunchedEffect(state.errorMessage) {
        if (state.errorMessage != null) {
            delay(3000)
            viewModel.clearError()
        }
    }
    LaunchedEffect(state.successMessage) {
        if (state.successMessage != null) {
            delay(3000)
            viewModel.clearSuccess()
        }
    }

    BoxWithConstraints(
        modifier = Modifier
            .fillMaxSize()
            .background(PageBackground),
    ) {
        val cardMaxHeight = maxHeight * 0.92f // 콘텐츠가 화면 92% 넘으면 스크롤

        // ── 중앙 흰색 카드 (웹 auth-form) ──
        Surface(
            modifier = Modifier
                .align(Alignment.Center)
                .fillMaxWidth()
                .heightIn(max = cardMaxHeight)
                .padding(horizontal = 24.dp),
            shape = RoundedCornerShape(20.dp),
            color = SurfaceWhite,
            shadowElevation = 16.dp,
        ) {
            Column(
                modifier = Modifier
                    .verticalScroll(rememberScrollState()) // 콘텐츠가 화면 넘을 때만 스크롤
                    .padding(horizontal = 36.dp, vertical = 32.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp),
            ) {
                // 브랜드 (웹 auth-brand)
                Column(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalAlignment = Alignment.CenterHorizontally,
                ) {
                    Text(
                        text = "Job Tracker",
                        style = MaterialTheme.typography.headlineMedium.copy(
                            brush = BrandGradient,
                            fontSize = 26.sp,
                            fontWeight = FontWeight.Bold,
                        ),
                    )
                    Spacer(Modifier.height(6.dp))
                    Text(
                        text = "채용 공고 지원을 한눈에",
                        fontSize = 14.sp,
                        color = TextGray,
                    )
                }

                Text(
                    text = if (state.isSignup) "회원가입" else "로그인",
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.Bold,
                    color = TextDark,
                )

                // 이메일
                FieldLabel("이메일")
                AuthTextField(
                    value = state.email,
                    onValueChange = viewModel::onEmailChange,
                    keyboardType = KeyboardType.Email,
                )

                // 비밀번호 (눈 아이콘 토글 — 웹과 동일)
                FieldLabel("비밀번호")
                var showPassword by remember { mutableStateOf(false) }
                AuthTextField(
                    value = state.password,
                    onValueChange = viewModel::onPasswordChange,
                    visualTransformation = if (showPassword) {
                        VisualTransformation.None
                    } else {
                        PasswordVisualTransformation()
                    },
                    trailingIcon = {
                        IconButton(onClick = { showPassword = !showPassword }) {
                            Icon(
                                imageVector = if (showPassword) {
                                    Icons.Filled.VisibilityOff
                                } else {
                                    Icons.Filled.Visibility
                                },
                                contentDescription = if (showPassword) "비밀번호 숨기기" else "비밀번호 보기",
                                tint = TextGray,
                            )
                        }
                    },
                )

                // 회원가입 모드 전용: 비밀번호 확인 + 닉네임
                if (state.isSignup) {
                    FieldLabel("비밀번호 확인")
                    var showPasswordConfirm by remember { mutableStateOf(false) }
                    AuthTextField(
                        value = state.passwordConfirm,
                        onValueChange = viewModel::onPasswordConfirmChange,
                        visualTransformation = if (showPasswordConfirm) {
                            VisualTransformation.None
                        } else {
                            PasswordVisualTransformation()
                        },
                        trailingIcon = {
                            IconButton(onClick = { showPasswordConfirm = !showPasswordConfirm }) {
                                Icon(
                                    imageVector = if (showPasswordConfirm) {
                                        Icons.Filled.VisibilityOff
                                    } else {
                                        Icons.Filled.Visibility
                                    },
                                    contentDescription = if (showPasswordConfirm) {
                                        "비밀번호 확인 숨기기"
                                    } else {
                                        "비밀번호 확인 보기"
                                    },
                                    tint = TextGray,
                                )
                            }
                        },
                    )

                    FieldLabel("닉네임")
                    AuthTextField(
                        value = state.nickname,
                        onValueChange = viewModel::onNicknameChange,
                        keyboardType = KeyboardType.Text,
                    )
                }

                // ── 그라디언트 버튼 (웹 primary-button) ──
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(48.dp)
                        .clip(RoundedCornerShape(10.dp))
                        .background(BrandGradient)
                        .clickable(enabled = !state.isLoading) { viewModel.submit() },
                    contentAlignment = Alignment.Center,
                ) {
                    if (state.isLoading) {
                        CircularProgressIndicator(
                            modifier = Modifier.size(20.dp),
                            color = Color.White,
                            strokeWidth = 2.dp,
                        )
                    } else {
                        Text(
                            text = if (state.isSignup) "가입하기" else "로그인",
                            color = Color.White,
                            fontSize = 16.sp,
                            fontWeight = FontWeight.SemiBold,
                        )
                    }
                }

                // ── 모드 전환 링크 (웹 link-button) ──
                Text(
                    text = if (state.isSignup) "로그인으로 돌아가기" else "계정이 없나요? 회원가입",
                    modifier = Modifier
                        .fillMaxWidth()
                        .clickable { viewModel.toggleMode() }
                        .padding(vertical = 4.dp),
                    textAlign = TextAlign.Center,
                    color = Indigo,
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Medium,
                )
            }
        }

        // ── 토스트 (상단 오버레이 — UI 밀지 않음) ──
        state.errorMessage?.let { message ->
            Toast(message = message, isError = true)
        }
        state.successMessage?.let { message ->
            Toast(message = message, isError = false)
        }
    }
}

@Composable
private fun FieldLabel(text: String) {
    Text(
        text = text,
        fontSize = 14.sp,
        fontWeight = FontWeight.Medium,
        color = TextDark,
    )
}

@Composable
private fun AuthTextField(
    value: String,
    onValueChange: (String) -> Unit,
    modifier: Modifier = Modifier,
    keyboardType: KeyboardType = KeyboardType.Text,
    visualTransformation: VisualTransformation = VisualTransformation.None,
    trailingIcon: (@Composable () -> Unit)? = null,
) {
    OutlinedTextField(
        value = value,
        onValueChange = onValueChange,
        modifier = modifier.fillMaxWidth(),
        singleLine = true,
        shape = RoundedCornerShape(10.dp),
        keyboardOptions = KeyboardOptions(keyboardType = keyboardType),
        visualTransformation = visualTransformation,
        trailingIcon = trailingIcon,
        textStyle = TextStyle(fontSize = 15.sp),
        colors = OutlinedTextFieldDefaults.colors(
            focusedBorderColor = Indigo,
            unfocusedBorderColor = BorderGray,
            cursorColor = Indigo,
        ),
    )
}

@Composable
private fun BoxScope.Toast(message: String, isError: Boolean) {
    Surface(
        modifier = Modifier
            .align(Alignment.TopCenter)
            .padding(top = 48.dp),
        shape = RoundedCornerShape(10.dp),
        color = if (isError) ErrorRed else SuccessGreen,
        shadowElevation = 6.dp,
    ) {
        Text(
            text = message,
            color = Color.White,
            fontSize = 14.sp,
            modifier = Modifier.padding(horizontal = 20.dp, vertical = 10.dp),
        )
    }
}
