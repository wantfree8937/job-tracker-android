package com.wantfree.jobtracker.domain.repository

import com.wantfree.jobtracker.data.model.job.JobPostingResponse

/**
 * 공고 저장소 인터페이스 (클린 아키텍처 — domain 계층)
 * 구현체는 data 계층(JobRepositoryImpl)에 있다.
 */
interface JobRepository {

    suspend fun getJobs(status: String?, keyword: String?): Result<List<JobPostingResponse>>

    suspend fun getStats(): Result<Map<String, Long>>
}
