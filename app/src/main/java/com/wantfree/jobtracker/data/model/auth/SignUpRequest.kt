package com.wantfree.jobtracker.data.model.auth

import kotlinx.serialization.Serializable

/** 회원가입 요청 DTO — 백엔드 SignUpRequest와 동일 (password 8자 이상, nickname 2~20자) */
@Serializable
data class SignUpRequest(
    val email: String,
    val password: String,
    val nickname: String,
)
