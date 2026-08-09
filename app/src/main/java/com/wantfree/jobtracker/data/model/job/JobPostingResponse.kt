package com.wantfree.jobtracker.data.model.job

import kotlinx.serialization.Serializable

/** 공고 응답 DTO — 백엔드 JobPostingResponse와 동일. link/deadline/memo는 서버가 null로 보낼 수 있음 */
@Serializable
data class JobPostingResponse(
    val id: Long,
    val companyName: String,
    val position: String,
    val link: String? = null,
    val deadline: String? = null,
    val status: String,
    val memo: String? = null,
    val createdAt: String,
    val updatedAt: String,
)
