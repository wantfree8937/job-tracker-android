package com.wantfree.jobtracker.data.model.auth

import kotlinx.serialization.Serializable

/** 로그인 요청 DTO — 백엔드 LoginRequest와 동일 */
@Serializable
data class LoginRequest(
    val email: String,
    val password: String,
)
