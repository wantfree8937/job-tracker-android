package com.wantfree.jobtracker.data.repository

import com.wantfree.jobtracker.data.api.JobService
import com.wantfree.jobtracker.data.model.job.JobPostingResponse
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
}
