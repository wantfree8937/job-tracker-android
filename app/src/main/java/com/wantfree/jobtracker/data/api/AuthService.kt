package com.wantfree.jobtracker.data.api

import com.wantfree.jobtracker.data.model.auth.KeywordsRequest
import com.wantfree.jobtracker.data.model.auth.LoginRequest
import com.wantfree.jobtracker.data.model.auth.SignUpRequest
import com.wantfree.jobtracker.data.model.auth.TokenResponse
import com.wantfree.jobtracker.data.model.auth.UserResponse
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.PUT

/** 인증 API — 백엔드 AuthController와 1:1 대응 */
interface AuthService {

    @POST("api/auth/login")
    suspend fun login(@Body request: LoginRequest): TokenResponse

    @POST("api/auth/signup")
    suspend fun signUp(@Body request: SignUpRequest): UserResponse

    @GET("api/auth/me")
    suspend fun getMe(): UserResponse

    @PUT("api/auth/me/keywords")
    suspend fun updateKeywords(@Body request: KeywordsRequest): UserResponse
}
