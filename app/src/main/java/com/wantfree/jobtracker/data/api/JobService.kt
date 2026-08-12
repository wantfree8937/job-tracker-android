package com.wantfree.jobtracker.data.api

import com.wantfree.jobtracker.data.model.job.CollectedJobLoadResult
import com.wantfree.jobtracker.data.model.job.CollectedJobResponse
import com.wantfree.jobtracker.data.model.job.CrawlRequest
import com.wantfree.jobtracker.data.model.job.JobPostingRequest
import com.wantfree.jobtracker.data.model.job.JobPostingResponse
import com.wantfree.jobtracker.data.model.job.JobPostingUpdateRequest
import com.wantfree.jobtracker.data.model.job.JobSearchRequest
import com.wantfree.jobtracker.data.model.job.JobSearchResult
import retrofit2.http.Body
import retrofit2.http.DELETE
import retrofit2.http.GET
import retrofit2.http.PATCH
import retrofit2.http.POST
import retrofit2.http.Path
import retrofit2.http.Query

/** 공고 API — 백엔드 JobPostingController와 1:1 대응 */
interface JobService {

    @GET("api/jobs")
    suspend fun getJobs(
        @Query("status") status: String? = null,
        @Query("keyword") keyword: String? = null,
    ): List<JobPostingResponse>

    // 응답이 { "WISH": 3, ... } 형태의 순수 Map이라 래퍼 DTO 없이 바로 받는다
    @GET("api/jobs/stats")
    suspend fun getStats(): Map<String, Long>

    @GET("api/jobs/{id}")
    suspend fun getJob(@Path("id") id: Long): JobPostingResponse

    @POST("api/jobs")
    suspend fun createJob(@Body request: JobPostingRequest): JobPostingResponse

    // 백엔드가 PATCH로 부분 수정 처리 (PUT 아님)
    @PATCH("api/jobs/{id}")
    suspend fun updateJob(@Path("id") id: Long, @Body request: JobPostingUpdateRequest): JobPostingResponse

    @DELETE("api/jobs/{id}")
    suspend fun deleteJob(@Path("id") id: Long)

    @GET("api/jobs/collected")
    suspend fun getCollectedJobs(@Query("keyword") keyword: String? = null): List<CollectedJobResponse>

    @POST("api/jobs/collected/{id}/scrap")
    suspend fun scrapCollectedJob(@Path("id") id: Long): JobPostingResponse

    @POST("api/jobs/collect/search")
    suspend fun searchCollectedJobs(@Body request: JobSearchRequest): JobSearchResult

    @POST("api/jobs/collected/crawl")
    suspend fun crawlCollected(@Body request: CrawlRequest = CrawlRequest()): CollectedJobLoadResult
}
