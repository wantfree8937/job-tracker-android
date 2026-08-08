package com.wantfree.jobtracker.data.model.auth

import kotlinx.serialization.Serializable

/** 로그인 성공 응답 DTO — 백엔드 TokenResponse와 동일 */
@Serializable
data class TokenResponse(
    val accessToken: String,
    val tokenType: String,
    val expiresIn: Long,
)
