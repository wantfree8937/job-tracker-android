package com.wantfree.jobtracker.data.repository

import com.wantfree.jobtracker.data.api.JobService
import com.wantfree.jobtracker.data.model.job.CollectedJobResponse
import com.wantfree.jobtracker.data.model.job.JobPostingRequest
import com.wantfree.jobtracker.data.model.job.JobPostingResponse
import com.wantfree.jobtracker.data.model.job.JobPostingUpdateRequest
import com.wantfree.jobtracker.data.model.job.JobSearchRequest
import com.wantfree.jobtracker.data.model.job.JobSearchResult
import com.wantfree.jobtracker.domain.repository.JobRepository
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class JobRepositoryImpl @Inject constructor(
    private val jobService: JobService,
) : JobRepository {

    override suspend fun getJobs(status: String?, keyword: String?): Result<List<JobPostingResponse>> =
        runCatching { jobService.getJobs(status = status, keyword = keyword) }

    override suspend fun getStats(): Result<Map<String, Long>> =
        runCatching { jobService.getStats() }

    override suspend fun getJob(id: Long): Result<JobPostingResponse> =
        runCatching { jobService.getJob(id) }

    override suspend fun createJob(request: JobPostingRequest): Result<JobPostingResponse> =
        runCatching { jobService.createJob(request) }

    override suspend fun updateJob(id: Long, request: JobPostingUpdateRequest): Result<JobPostingResponse> =
        runCatching { jobService.updateJob(id, request) }

    override suspend fun deleteJob(id: Long): Result<Unit> =
        runCatching { jobService.deleteJob(id) }

    override suspend fun getCollectedJobs(keyword: String?): Result<List<CollectedJobResponse>> =
        runCatching { jobService.getCollectedJobs(keyword) }

    override suspend fun scrapCollectedJob(id: Long): Result<JobPostingResponse> =
        runCatching { jobService.scrapCollectedJob(id) }

    override suspend fun searchCollectedJobs(keyword: String): Result<JobSearchResult> =
        runCatching { jobService.searchCollectedJobs(JobSearchRequest(keyword)) }
}
