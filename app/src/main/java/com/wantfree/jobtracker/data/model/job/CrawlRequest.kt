package com.wantfree.jobtracker.data.model.job

import kotlinx.serialization.Serializable

/** 관심 키워드 전체 크롤링 요청 DTO — 백엔드 JobCollectController와 동일 (keywords 비우면 사용자 관심 키워드 사용) */
@Serializable
data class CrawlRequest(val keywords: List<String> = emptyList())
