package com.wantfree.jobtracker.data.model.job

import kotlinx.serialization.Serializable

/** 키워드 공고 수집 결과 DTO — 백엔드 JobCollectController와 동일 */
@Serializable
data class JobSearchResult(val keyword: String, val collected: Int, val skipped: Int)
