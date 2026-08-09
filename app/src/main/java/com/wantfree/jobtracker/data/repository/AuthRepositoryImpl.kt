package com.wantfree.jobtracker.data.repository

import com.wantfree.jobtracker.data.api.AuthService
import com.wantfree.jobtracker.data.local.TokenManager
import com.wantfree.jobtracker.data.model.auth.KeywordsRequest
import com.wantfree.jobtracker.data.model.auth.LoginRequest
import com.wantfree.jobtracker.data.model.auth.SignUpRequest
import com.wantfree.jobtracker.data.model.auth.TokenResponse
import com.wantfree.jobtracker.data.model.auth.UserResponse
import com.wantfree.jobtracker.domain.repository.AuthRepository
import javax.inject.Inject
import javax.inject.Singleton

/**
 * AuthRepository 구현체 (data 계층)
 * runCatching으로 예외를 Result로 감싸서 UI가 쉽게 처리하게 한다.
 */
@Singleton
class AuthRepositoryImpl @Inject constructor(
    private val authService: AuthService,
    private val tokenManager: TokenManager,
) : AuthRepository {

    override suspend fun login(email: String, password: String): Result<TokenResponse> = runCatching {
        val response = authService.login(LoginRequest(email = email, password = password))
        tokenManager.saveToken(response.accessToken)
        response
    }

    override suspend fun signUp(email: String, password: String, nickname: String): Result<UserResponse> =
        runCatching {
            authService.signUp(SignUpRequest(email = email, password = password, nickname = nickname))
        }

    override suspend fun logout() {
        tokenManager.clearToken()
    }

    override suspend fun getMe(): Result<UserResponse> = runCatching { authService.getMe() }

    override suspend fun updateKeywords(keywords: List<String>): Result<UserResponse> =
        runCatching { authService.updateKeywords(KeywordsRequest(keywords)) }
}
