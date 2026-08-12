package com.wantfree.jobtracker.data.model.job

import kotlinx.serialization.Serializable

/** 관심 키워드 전체 크롤링 결과 DTO — 백엔드 JobCollectController와 동일 */
@Serializable
data class CollectedJobLoadResult(val loaded: Int, val skipped: Int)
