package com.wantfree.jobtracker.core.di

import com.jakewharton.retrofit2.converter.kotlinx.serialization.asConverterFactory
import com.wantfree.jobtracker.data.api.AiService
import com.wantfree.jobtracker.data.api.AuthService
import com.wantfree.jobtracker.data.api.JobService
import com.wantfree.jobtracker.data.local.AuthInterceptor
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import kotlinx.serialization.json.Json
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.OkHttpClient
import retrofit2.Retrofit
import java.util.concurrent.TimeUnit
import javax.inject.Singleton

/** 네트워크 계층 DI — Retrofit, OkHttp, 직렬화 설정 */
@Module
@InstallIn(SingletonComponent::class)
object NetworkModule {

    private const val BASE_URL = "https://job-tracker-so4v.onrender.com/"

    @Provides
    @Singleton
    fun provideJson(): Json = Json {
        ignoreUnknownKeys = true // 백엔드가 필드 추가해도 안전
        coerceInputValues = true // 서버가 null로 보내도 기본값으로 안전 처리
    }

    @Provides
    @Singleton
    fun provideOkHttpClient(authInterceptor: AuthInterceptor): OkHttpClient = OkHttpClient.Builder()
        .addInterceptor(authInterceptor)
        // Render 무료 플랜 콜드 스타트(최대 50초+) 대비 타임아웃 넉넉히
        .connectTimeout(90, TimeUnit.SECONDS)
        .readTimeout(90, TimeUnit.SECONDS)
        .build()

    @Provides
    @Singleton
    fun provideRetrofit(client: OkHttpClient, json: Json): Retrofit =
        Retrofit.Builder()
            .baseUrl(BASE_URL)
            .client(client)
            .addConverterFactory(json.asConverterFactory("application/json".toMediaType()))
            .build()

    @Provides
    @Singleton
    fun provideAuthService(retrofit: Retrofit): AuthService =
        retrofit.create(AuthService::class.java)

    @Provides
    @Singleton
    fun provideJobService(retrofit: Retrofit): JobService =
        retrofit.create(JobService::class.java)

    @Provides
    @Singleton
    fun provideAiService(retrofit: Retrofit): AiService =
        retrofit.create(AiService::class.java)
}
