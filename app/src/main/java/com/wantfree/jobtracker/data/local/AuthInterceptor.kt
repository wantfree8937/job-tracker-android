package com.wantfree.jobtracker.data.local

import kotlinx.coroutines.flow.first
import kotlinx.coroutines.runBlocking
import okhttp3.Interceptor
import okhttp3.Response
import javax.inject.Inject

/** 저장된 JWT가 있으면 모든 요청에 Authorization 헤더를 붙인다 */
class AuthInterceptor @Inject constructor(
    private val tokenManager: TokenManager,
) : Interceptor {

    override fun intercept(chain: Interceptor.Chain): Response {
        val token = runBlocking { tokenManager.accessToken.first() }
        val original = chain.request()
        val request = if (token != null) {
            original.newBuilder()
                .addHeader("Authorization", "Bearer $token")
                .build()
        } else {
            original
        }
        val response = chain.proceed(request)
        if (response.code == 401) {
            // 토큰 만료/무효 — 저장된 토큰만 삭제 (다음 앱 시작 때 로그인 화면으로 자연스럽게 이동)
            runBlocking { tokenManager.clearToken() }
        }
        return response
    }
}
