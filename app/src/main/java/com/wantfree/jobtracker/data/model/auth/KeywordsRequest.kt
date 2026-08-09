package com.wantfree.jobtracker.data.model.auth

import kotlinx.serialization.Serializable

/** 관심 분야 키워드 저장 요청 DTO — 백엔드 PUT /api/auth/me/keywords와 동일 */
@Serializable
data class KeywordsRequest(val keywords: List<String>)
