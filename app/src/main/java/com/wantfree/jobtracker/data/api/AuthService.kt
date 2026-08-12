package com.wantfree.jobtracker.data.api

import com.wantfree.jobtracker.data.model.auth.KeywordsRequest
import com.wantfree.jobtracker.data.model.auth.LoginRequest
import com.wantfree.jobtracker.data.model.auth.ProfileFileResponse
import com.wantfree.jobtracker.data.model.auth.ProfileResponse
import com.wantfree.jobtracker.data.model.auth.ProfileUpdateRequest
import com.wantfree.jobtracker.data.model.auth.SignUpRequest
import com.wantfree.jobtracker.data.model.auth.TokenResponse
import com.wantfree.jobtracker.data.model.auth.UserResponse
import okhttp3.MultipartBody
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.DELETE
import retrofit2.http.GET
import retrofit2.http.Multipart
import retrofit2.http.POST
import retrofit2.http.PUT
import retrofit2.http.Part
import retrofit2.http.Path

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

    @GET("api/auth/me/profile")
    suspend fun getProfile(): ProfileResponse

    @PUT("api/auth/me/profile")
    suspend fun updateProfile(@Body request: ProfileUpdateRequest): ProfileResponse

    @Multipart
    @PUT("api/auth/me/profile/file")
    suspend fun uploadProfileFile(@Part file: MultipartBody.Part): ProfileFileResponse

    @GET("api/auth/me/profile/files")
    suspend fun getProfileFiles(): List<ProfileFileResponse>

    @DELETE("api/auth/me/profile/file/{fileId}")
    suspend fun deleteProfileFile(@Path("fileId") fileId: Long): Response<Unit>
}
