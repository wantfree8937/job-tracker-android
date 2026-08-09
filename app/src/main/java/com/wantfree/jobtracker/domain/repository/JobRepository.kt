package com.wantfree.jobtracker.domain.repository

import com.wantfree.jobtracker.data.model.job.CollectedJobResponse
import com.wantfree.jobtracker.data.model.job.JobPostingRequest
import com.wantfree.jobtracker.data.model.job.JobPostingResponse
import com.wantfree.jobtracker.data.model.job.JobPostingUpdateRequest
import com.wantfree.jobtracker.data.model.job.JobSearchResult

/**
 * 공고 저장소 인터페이스 (클린 아키텍처 — domain 계층)
 * 구현체는 data 계층(JobRepositoryImpl)에 있다.
 */
interface JobRepository {

    suspend fun getJobs(status: String?, keyword: String?): Result<List<JobPostingResponse>>

    suspend fun getStats(): Result<Map<String, Long>>

    suspend fun getJob(id: Long): Result<JobPostingResponse>

    suspend fun createJob(request: JobPostingRequest): Result<JobPostingResponse>

    suspend fun updateJob(id: Long, request: JobPostingUpdateRequest): Result<JobPostingResponse>

    suspend fun deleteJob(id: Long): Result<Unit>

    suspend fun getCollectedJobs(): Result<List<CollectedJobResponse>>

    suspend fun scrapCollectedJob(id: Long): Result<JobPostingResponse>

    suspend fun searchCollectedJobs(keyword: String): Result<JobSearchResult>

    suspend fun getMyKeywords(): Result<List<String>>
}
