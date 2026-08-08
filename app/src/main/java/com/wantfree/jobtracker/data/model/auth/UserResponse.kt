package com.wantfree.jobtracker.data.model.auth

import kotlinx.serialization.Serializable

/**
 * 사용자 정보 응답 DTO — 백엔드 UserResponse와 동일
 * createdAt은 ISO-8601 문자열로 받는다 (LocalDateTime 직렬화 모듈 없이 단순하게)
 */
@Serializable
data class UserResponse(
    val id: Long,
    val email: String,
    val nickname: String,
    val createdAt: String,
    val keywords: List<String> = emptyList(),
)
