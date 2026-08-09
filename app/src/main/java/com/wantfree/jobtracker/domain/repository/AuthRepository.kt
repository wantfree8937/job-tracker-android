package com.wantfree.jobtracker.domain.repository

import com.wantfree.jobtracker.data.model.auth.TokenResponse
import com.wantfree.jobtracker.data.model.auth.UserResponse

/**
 * 인증 저장소 인터페이스 (클린 아키텍처 — domain 계층)
 * 구현체는 data 계층(AuthRepositoryImpl)에 있다.
 * UI(ViewModel)는 이 인터페이스만 바라본다.
 */
interface AuthRepository {

    /** 로그인 성공 시 JWT 토큰을 로컬에 저장한다 */
    suspend fun login(email: String, password: String): Result<TokenResponse>

    /** 회원가입 후 자동 로그인은 하지 않는다 (가입 → 로그인 화면으로) */
    suspend fun signUp(email: String, password: String, nickname: String): Result<UserResponse>

    /** 저장된 토큰 삭제 (로그아웃) */
    suspend fun logout()

    /** 내 정보 조회 (관심 분야 포함) */
    suspend fun getMe(): Result<UserResponse>

    /** 관심 분야 저장 */
    suspend fun updateKeywords(keywords: List<String>): Result<UserResponse>
}
