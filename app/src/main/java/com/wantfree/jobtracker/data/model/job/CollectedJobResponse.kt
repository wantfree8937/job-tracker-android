package com.wantfree.jobtracker.data.model.job

import kotlinx.serialization.Serializable

/** 수집 공고 응답 DTO — 백엔드 CollectedJobResponse와 동일 */
@Serializable
data class CollectedJobResponse(
    val id: Long,
    val company: String,
    val title: String,
    val url: String,
    val source: String,
    val jobKey: String,
    val region: String? = null,
    val experience: String? = null,
    val industry: String? = null,
    val createdAt: String,
    val scrapedByMe: Boolean,
)
