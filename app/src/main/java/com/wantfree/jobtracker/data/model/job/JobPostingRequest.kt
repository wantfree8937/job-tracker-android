package com.wantfree.jobtracker.data.model.job

import kotlinx.serialization.Serializable

/** 공고 생성 요청 DTO — 백엔드 JobPostingRequest와 동일 */
@Serializable
data class JobPostingRequest(
    val companyName: String,
    val position: String,
    val link: String? = null,
    val deadline: String? = null, // "YYYY-MM-DD"
    val status: String = "WISH",
    val memo: String? = null,
    val region: String? = null,
    val experience: String? = null,
    val industry: String? = null,
)
