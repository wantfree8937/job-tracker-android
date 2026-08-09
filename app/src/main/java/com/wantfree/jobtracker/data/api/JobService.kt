package com.wantfree.jobtracker.data.api

import com.wantfree.jobtracker.data.model.job.JobPostingResponse
import retrofit2.http.GET
import retrofit2.http.Query

/** 공고 API — 백엔드 JobController와 1:1 대응 (목록/통계 조회만) */
interface JobService {

    @GET("api/jobs")
    suspend fun getJobs(
        @Query("status") status: String? = null,
        @Query("keyword") keyword: String? = null,
    ): List<JobPostingResponse>

    // 응답이 { "WISH": 3, ... } 형태의 순수 Map이라 래퍼 DTO 없이 바로 받는다
    @GET("api/jobs/stats")
    suspend fun getStats(): Map<String, Long>
}
