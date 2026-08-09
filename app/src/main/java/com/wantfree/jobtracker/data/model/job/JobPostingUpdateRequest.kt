package com.wantfree.jobtracker.data.model.job

import kotlinx.serialization.Serializable

/** 공고 부분 수정 요청 DTO — 백엔드 JobPostingUpdateRequest와 동일. null 필드는 반영되지 않는다 */
@Serializable
data class JobPostingUpdateRequest(
    val companyName: String? = null,
    val position: String? = null,
    val link: String? = null,
    val deadline: String? = null, // "YYYY-MM-DD"
    val status: String? = null,
    val memo: String? = null,
)
