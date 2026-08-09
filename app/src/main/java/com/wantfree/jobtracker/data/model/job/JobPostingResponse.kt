package com.wantfree.jobtracker.data.model.job

import kotlinx.serialization.Serializable

/** 공고 응답 DTO — 백엔드 JobPostingResponse와 동일 */
@Serializable
data class JobPostingResponse(
    val id: Long,
    val companyName: String,
    val position: String,
    val link: String,
    val deadline: String,
    val status: String,
    val memo: String,
    val createdAt: String,
    val updatedAt: String,
)
