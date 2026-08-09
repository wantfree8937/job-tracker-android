package com.wantfree.jobtracker.data.model.job

import kotlinx.serialization.Serializable

/** 키워드로 공고 수집 요청 DTO — 백엔드 JobCollectController와 동일 */
@Serializable
data class JobSearchRequest(val keyword: String)
